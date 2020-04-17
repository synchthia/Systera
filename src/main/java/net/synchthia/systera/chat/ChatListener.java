package net.synchthia.systera.chat;

import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.SysteraPlayer;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

        String format = String.format("%s&7%s&a:&r ", systeraPlayer.getPrefix(), player.getDisplayName());
        String japanizeMsg = "";
        if (systeraPlayer.getSettings().getJapanize().getValue()) {
            String converted = japanize.convert(event.getMessage());
            if (!converted.isEmpty()) {
                japanizeMsg = ChatColor.GOLD + " (" + converted + ChatColor.GOLD + ")";
            }
        }

        event.setFormat(StringUtil.coloring(format) + "%2$s" + japanizeMsg);
    }
}
