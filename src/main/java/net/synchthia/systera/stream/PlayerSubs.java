package net.synchthia.systera.stream;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.logging.Level;

public class PlayerSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.PlayerStream stream = APIClient.playerStreamFromJson(message);
        assert stream != null;
        if (stream.getType() == SysteraProtos.PlayerStream.Type.GROUPS) {
            UUID playerUUID = APIClient.toUUID(stream.getEntry().getUuid());
            plugin.getPlayerStore().get(playerUUID).setGroups(stream.getEntry().getGroupsList().stream().toList());
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
