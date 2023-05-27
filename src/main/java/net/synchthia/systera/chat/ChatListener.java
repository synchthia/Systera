package net.synchthia.systera.chat;

import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.SysteraPlayer;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.logging.Level;

public class ChatListener implements Listener {
    private final SysteraPlugin plugin;
    private final Japanize japanize;

    public ChatListener(SysteraPlugin plugin) {
        this.plugin = plugin;
        this.japanize = new Japanize();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        SysteraPlayer systeraPlayer = plugin.getPlayerStore().get(player.getUniqueId());
        boolean isGlobal = SysteraPlugin.isEnableGlobalChat() && systeraPlayer.getSettings().getGlobalChat().getValue();

        String format = String.format("%s&7%s&a:&r ", isGlobal ? "" : "&8[local]", player.getDisplayName());
        String japanizeMsg = "";
        if (systeraPlayer.getSettings().getJapanize().getValue()) {
            String converted = japanize.convert(event.getMessage());
            if (!converted.isEmpty()) {
                japanizeMsg = ChatColor.GOLD + " (" + converted + ChatColor.GOLD + ")";
            }
        }

        event.setFormat(StringUtil.coloring(format) + "%2$s" + japanizeMsg);

        if (isGlobal) {
            plugin.getApiClient().chat(APIClient.buildPlayerIdentity(player.getUniqueId().toString(), player.getDisplayName()), SysteraPlugin.getServerId(), event.getMessage() + japanizeMsg).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    plugin.getLogger().log(Level.WARNING, "Failed send global chat event", throwable);
                }
            });
        }
    }
}
