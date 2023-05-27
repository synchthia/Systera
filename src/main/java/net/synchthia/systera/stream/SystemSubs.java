package net.synchthia.systera.stream;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.announce.Announce;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class SystemSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.SystemStream stream = APIClient.systemStreamFromJson(message);
        assert stream != null;
        switch (stream.getType()) {
            case DISPATCH:
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    SysteraPlugin.getInstance().getLogger().log(Level.INFO, "Incoming Command: " + stream.getMsg());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), stream.getMsg());
                });
                break;
            case ANNOUNCE:
                for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                    Announce announce = new Announce(onlinePlayer, stream.getMsg());
                    announce.sendAnnounce();
                }
                break;
        }
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        plugin.getLogger().log(Level.INFO, "P Subscribed : " + pattern);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        plugin.getLogger().log(Level.INFO, "P UN Subscribed : " + pattern);
    }
}
