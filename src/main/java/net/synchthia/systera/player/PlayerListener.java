package net.synchthia.systera.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class PlayerListener implements Listener {
    private static final Component ERROR_INTERRUPTED = MiniMessage.miniMessage().deserialize("<red>Currently not available:</red> <gray>[ERR_INTERRUPTED]</gray>");
    private static final Component ERROR_EXECUTION = MiniMessage.miniMessage().deserialize("<red>Currently not available:</red> <gray>[ERR_EXECUTION]</gray>");
    private static final Component ERROR_TIMEOUT = MiniMessage.miniMessage().deserialize("<red>Currently not available:</red> <gray>[ERR_TIMEOUT]</gray>");
    private static final Component ERROR_LOOKUP = MiniMessage.miniMessage().deserialize("<red>Currently not Available:</red> <gray>[LOOKUP_ERROR]</gray>");

    private final SysteraPlugin plugin;

    public PlayerListener(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.isStarted()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Server is Starting..."));
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED) && !event.getResult().equals(PlayerLoginEvent.Result.KICK_WHITELIST)) {
            return;
        }

        // Check punish
        List<SysteraProtos.PunishEntry> punishList = null;
        try {
            punishList = plugin.getPunishAPI().lookup(player.getUniqueId(), SysteraProtos.PunishLevel.TEMPBAN).get(5, TimeUnit.SECONDS).getEntryList();

            if (punishList.size() != 0) {
                SysteraProtos.PunishEntry entry = punishList.get(punishList.size() - 1);
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, plugin.getPunishAPI().message(player.getUniqueId(), entry.getLevel(), entry.getReason(), entry.getDate(), entry.getExpire()));
                return;
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            plugin.getLogger().log(Level.SEVERE, "Exception threw executing check punish", e);
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ERROR_LOOKUP);
        }

        // Init Player Profile
        SysteraPlayer pd = new SysteraPlayer(plugin, player);
        try {
            pd.init(event.getAddress().getHostAddress(), event.getAddress().getHostName()).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ERROR_INTERRUPTED);
        } catch (ExecutionException e) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ERROR_EXECUTION);
        } catch (TimeoutException e) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ERROR_TIMEOUT);
        }

        if (plugin.getPlayerStore().get(player.getUniqueId()) != null) {
            plugin.getPlayerStore().remove(player.getUniqueId());
        }

        plugin.getPlayerStore().add(player.getUniqueId(), pd);
        pd.applyPermissionsByGroup();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.joinMessage(Component.empty());

        // Whitelist
        if (player.hasPermission("minecraft.command.whitelist") && plugin.getServer().hasWhitelist()) {
            I18n.sendMessage(player, "whitelist.notify");
        }

        // Vanish
        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
        boolean isVanish = sp.getSettings().getVanish().getValue();

        if (player.hasPermission(sp.getSettings().getVanish().getPermission())) {
            if (isVanish) {
                I18n.sendMessage(player, "vanish.notify");
            }

            runVanishTask(sp, isVanish);
        } else {
            if (isVanish) {
                // Force disable vanish when removed perms
                sp.getSettings().getVanish().setValue(player, false);
            }

            // Hide Player
            plugin.getPlayerStore().list().stream().filter(p -> p.getSettings().getVanish().getValue()).forEach(targetSp -> {
                if (player.getPlayer() != null) {
                    player.getPlayer().hidePlayer(plugin, targetSp.getPlayer());
                }
            });
        }

        if (!isVanish) {
            plugin.getPlayerStore().list().stream().filter(p -> p.getSettings().getJoinMessage().getValue()).forEach(p ->
                    p.getPlayer().sendMessage(Component.text("Join> " + player.getName()).color(NamedTextColor.GRAY))
            );
        }

        // Current server
        plugin.getApiClient().setPlayerServer(player.getUniqueId(), SysteraPlugin.getServerId()).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed update player server");
                throwable.printStackTrace();
            }
        }));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());

        event.quitMessage(Component.empty());

        if (sp.getSettings().getVanish().getValue()) {
            // Remove Effect
            sp.getSettings().getVanish().applyVanishEffect(player, false);
        } else {
            plugin.getPlayerStore().list().stream().filter(p -> p.getSettings().getJoinMessage().getValue()).forEach(p ->
                    p.getPlayer().sendMessage(Component.text("Quit> " + player.getName()).color(NamedTextColor.GRAY))
            );
        }

        // Current server
        plugin.getApiClient().quitServer(player.getUniqueId(), SysteraPlugin.getServerId()).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed update player server");
                throwable.printStackTrace();
            }
        }));

        plugin.getPlayerStore().remove(player.getUniqueId());
    }

    private void runVanishTask(SysteraPlayer sp, boolean isVanish) {
        Player player = sp.getPlayer();
        sp.getSettings().getVanish().setValue(player, isVanish);

        // Re-apply vanish effect for HuskSync
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            sp.getSettings().getVanish().setValue(player, sp.getSettings().getVanish().getValue());
        }, 30L);
    }
}
