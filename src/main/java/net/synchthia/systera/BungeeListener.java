package net.synchthia.systera;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeListener implements PluginMessageListener {
    private final SysteraPlugin plugin;

    public BungeeListener(SysteraPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
    }
}
