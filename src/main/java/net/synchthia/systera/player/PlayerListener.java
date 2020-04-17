package net.synchthia.systera.player;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerListener implements Listener {
    private static final String ERROR_INTERRUPTED = ChatColor.RED + "Currently not available: " + ChatColor.GRAY + "[INTERRUPTED]";
    private static final String ERROR_EXECUTION = ChatColor.RED + "Currently not available: " + ChatColor.GRAY + "[EXECUTION]";
    private static final String ERROR_TIMEOUT = ChatColor.RED + "Currently not available: " + ChatColor.GRAY + "[TIMEOUT]";
    private final SysteraPlugin plugin;

    public PlayerListener(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.isStarted()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server is Starting...");
            return;
        }

        // TODO: Fetch Punish Status
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED) && !event.getResult().equals(PlayerLoginEvent.Result.KICK_WHITELIST)) {
            return;
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
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null);

        // Vanish
        if (!player.hasPermission("systera.vanish")) {
            plugin.getPlayerStore().list().stream().filter(p -> p.getSettings().getVanish().getValue()).forEach(sp -> {
                if (player.getPlayer() != null) {
                    player.getPlayer().hidePlayer(plugin, sp.getPlayer());
                }
            });
        }

        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
        if (sp.getSettings().getVanish().getValue()) {
            plugin.getServer().getOnlinePlayers().stream()
                    .filter(p -> !p.hasPermission("systera.vanish"))
                    .forEach(p -> p.hidePlayer(plugin, player));
        } else {
            plugin.getPlayerStore().list().stream()
                    .filter(p -> p.getSettings().getJoinMessage().getValue())
                    .forEach(p -> p.getPlayer().sendMessage(ChatColor.GRAY + "Join> " + player.getName()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(null);

        if (!plugin.getPlayerStore().get(player.getUniqueId()).getSettings().getVanish().getValue()) {
            plugin.getPlayerStore().list().stream()
                    .filter(p -> p.getSettings().getJoinMessage().getValue())
                    .forEach(p -> p.getPlayer().sendMessage(ChatColor.GRAY + "Quit> " + player.getName()));
        }

        plugin.getPlayerStore().remove(player.getUniqueId());
    }
}
