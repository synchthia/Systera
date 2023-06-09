package net.synchthia.systera.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatMessage {
    public static String CHAT_FORMAT = "<channel><prefix><gray><player_name></gray><green>: </green>";
    public static String DEFAULT_MESSAGE_FORMAT = "<reset><original_message></reset>";
    public static String JAPANIZE_MESSAGE_FORMAT = "<reset><japanize_message></reset> <gray>(<original_message>)</gray>";

    public static List<TagResolver> getChatFormatResolvers(String channel, String prefix, String playerName) {
        return new ArrayList<>(Arrays.asList(
                // Channel
                channel.equals("") ?
                        // Global Disabled
                        Placeholder.component("channel", Component.empty()) :

                        // Global Enabled
                        Placeholder.component("channel", Component.text("[" + channel + "]").color(
                                // local -> dark_gray / global -> gray
                                channel.equals("local") ? NamedTextColor.DARK_GRAY : NamedTextColor.GRAY
                        )),

                // Prefix
                Placeholder.parsed("prefix", prefix),

                // Player
                Placeholder.parsed("player_name", playerName)
        ));
    }
}
