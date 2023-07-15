package net.synchthia.systera.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.SysteraPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.logging.Level;

import static net.synchthia.systera.messages.ChatMessage.*;

public class ChatListener implements Listener {
    private final SysteraPlugin plugin;
    private final Japanize japanize;

    public ChatListener(SysteraPlugin plugin) {
        this.plugin = plugin;
        this.japanize = new Japanize();
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        SysteraPlayer systeraPlayer = plugin.getPlayerStore().get(player.getUniqueId());
        boolean isGlobal = SysteraPlugin.isEnableGlobalChat() && systeraPlayer.getSettings().getGlobalChat().getValue();

        event.message(Component.empty());

        // Placeholders
        List<TagResolver> resolvers = getChatFormatResolvers(
                // Channel
                SysteraPlugin.isEnableGlobalChat() ? (isGlobal ? SysteraPlugin.getServerId() : "local") : "",

                MiniMessage.miniMessage().serialize(systeraPlayer.getPrefix()),

                // Player Name
                MiniMessage.miniMessage().serialize(player.displayName()));

        // Message
        resolvers.add(Placeholder.unparsed("original_message", event.signedMessage().message()));

        // Japanize
        String japanizeMsg = "";
        if (systeraPlayer.getSettings().getJapanize().getValue()) {
            String converted = japanize.convert(event.signedMessage().message());
            if (!converted.isEmpty()) {
                japanizeMsg = converted;
            }
        }

        resolvers.add(Placeholder.unparsed("japanize_message", japanizeMsg));

        // イベントプレイヤーが含まれているPIを受信者から消す
        for (Player receivePlayer : plugin.getServer().getOnlinePlayers()) {
            for (SysteraProtos.PlayerIdentity receivePI : plugin.getPlayerStore().get(receivePlayer.getUniqueId()).getIgnoreList()) {
                if (player.getUniqueId().equals(APIClient.toUUID(receivePI.getUuid()))) {
                    event.viewers().remove(receivePlayer);
                }
            }
        }

        // Component
        final Component localFormat = japanizeMsg.equals("") ?
                // Default
                MiniMessage.miniMessage().deserialize(CHAT_FORMAT + DEFAULT_MESSAGE_FORMAT, TagResolver.resolver(resolvers)) :
                // Japanize
                MiniMessage.miniMessage().deserialize(CHAT_FORMAT + JAPANIZE_MESSAGE_FORMAT, TagResolver.resolver(resolvers));

        final Component globalFormat = japanizeMsg.equals("") ?
                // Default
                MiniMessage.miniMessage().deserialize(DEFAULT_MESSAGE_FORMAT, TagResolver.resolver(resolvers)) :
                // Japanize
                MiniMessage.miniMessage().deserialize(JAPANIZE_MESSAGE_FORMAT, TagResolver.resolver(resolvers));


        event.renderer((source, sourceDisplayName, message, viewer) -> localFormat);

        if (isGlobal) {
            plugin.getApiClient().chat(APIClient.buildPlayerIdentity(player.getUniqueId(), MiniMessage.miniMessage().serialize(player.displayName())), SysteraPlugin.getServerId(), MiniMessage.miniMessage().serialize(globalFormat)).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    plugin.getLogger().log(Level.WARNING, "Failed send global chat event", throwable);
                }
            });
        }
    }
}