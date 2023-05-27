package net.synchthia.systera.stream;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.group.Group;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;

public class GroupSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.GroupStream stream = APIClient.groupStreamFromJson(message);
        assert stream != null;
        switch (stream.getType()) {
            case GROUP:
            case PERMISSIONS:
                plugin.getServer().getScheduler().runTask(plugin, () -> {

                    // Update group
                    plugin.getGroupStore().remove(stream.getGroupEntry().getGroupName());
                    plugin.getGroupStore().add(stream.getGroupEntry().getGroupName(), Group.fromProto(stream.getGroupEntry()));

                    // Update Permissions
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        plugin.getPlayerStore().get(player.getUniqueId()).applyPermissionsByGroup();
                    }
                });
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
