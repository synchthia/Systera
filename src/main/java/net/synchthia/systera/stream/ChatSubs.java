package net.synchthia.systera.stream;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.messages.ChatMessage;
import net.synchthia.systera.player.SysteraPlayer;
import net.synchthia.systera.settings.Settings;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.logging.Level;

import static net.synchthia.systera.messages.ChatMessage.CHAT_FORMAT;

public class ChatSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.ChatStream stream = APIClient.chatStreamFromJson(message);
        assert stream != null;
        switch (stream.getType()) {
            case CHAT:
                SysteraProtos.ChatEntry chatEntry = stream.getChatEntry();
                if (!SysteraPlugin.isEnableGlobalChat()) {
                    return;
                }

                if (chatEntry.getServerName().equals(SysteraPlugin.getServerId())) {
                    return;
                }

                // Resolvers
                List<TagResolver> resolvers = ChatMessage.getChatFormatResolvers(chatEntry.getServerName(), "", chatEntry.getAuthor().getName());

                // Format
                Component globalFormat = MiniMessage.miniMessage().deserialize(CHAT_FORMAT + chatEntry.getMessage(), TagResolver.resolver(resolvers));

                // Log
                plugin.getServer().getConsoleSender().sendMessage(Component.text("[GLOBAL_CHAT]").appendSpace().append(globalFormat));

                // Send to Player
                plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().getOnlinePlayers().forEach(player -> {
                    SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
                    Settings settings = sp.getSettings();
                    if (settings.getGlobalChat().getValue() && sp.getIgnoreList().stream().noneMatch(x -> APIClient.toUUID(chatEntry.getAuthor().getUuid()).equals(APIClient.toUUID(x.getUuid())))) {
                        player.sendMessage(globalFormat);
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
