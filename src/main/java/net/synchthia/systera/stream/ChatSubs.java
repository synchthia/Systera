package net.synchthia.systera.stream;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.settings.Settings;
import net.synchthia.systera.util.StringUtil;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;

public class ChatSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.ChatStream stream = APIClient.chatStreamFromJson(message);
        assert stream != null;
        switch (stream.getType()) {
            case CHAT:
                if (!SysteraPlugin.isEnableGlobalChat()) {
                    return;
                }

                if (stream.getChatEntry().getServerName().equals(SysteraPlugin.getServerId())) {
                    return;
                }

                // Log
                plugin.getLogger().log(Level.INFO, StringUtil.coloring(String.format("[GlobalChat] [%s]%s: %s", stream.getChatEntry().getServerName(), stream.getChatEntry().getAuthor().getName(), stream.getChatEntry().getMessage())));

                // Send to Player
                plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().getOnlinePlayers().forEach(player -> {
                    Settings settings = plugin.getPlayerStore().get(player.getUniqueId()).getSettings();
                    if (settings.getGlobalChat().getValue()) {
                        player.sendMessage(StringUtil.coloring(String.format("&7[%s]&r%s&a:&r %s", stream.getChatEntry().getServerName(), stream.getChatEntry().getAuthor().getName(), stream.getChatEntry().getMessage())));
                    }
                }));

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
