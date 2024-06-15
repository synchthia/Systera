package net.synchthia.systera;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.player.SysteraPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.processing.CommandContainer;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@CommandContainer(priority = 2)
public class CommandSuggestions {
    private final SysteraPlugin plugin;

    public CommandSuggestions(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    @Suggestions("players")
    public Stream<String> players(final CommandContext<CommandSender> context, final String input) {
        return plugin.getServer().getOnlinePlayers().stream().map((Player::getName));
    }

    @Suggestions("all_and_players")
    public Stream<String> allAndPlayers(final CommandContext<CommandSender> context, final String input) {
        List<String> players = new ArrayList<>(plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
        players.add("*");
        return players.stream();
    }

    @Suggestions("punish_reason")
    public Stream<String> punishReason(final CommandContext<CommandSender> context, final String input) {
        return Stream.of(
                "Chat Spam (チャットスパム)",
                "Glitch (バグや不具合の意図的な不正利用)",
                "NSFW Content (不適切なコンテンツ) ",
                "Griefing (他のユーザーへの迷惑行為)",
                "Violent Language (不適切な発言)",
                "Hack / Cheat (チート行為)",
                "Others / その他 -> ..."
        );
    }

    @Suggestions("player_settings")
    public Stream<String> playerSettings(final CommandContext<CommandSender> context, final String input) {
        if (context.sender() instanceof Player player) {
            SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
            return sp.getSettings().getSettings().keySet().stream().filter((x) -> sp.getSettings().getSettings().get(x).hasPermission(player));
        }
        return Stream.of();
    }

    @Suggestions("ignored_players")
    public Stream<String> ignoredPlayers(final CommandContext<CommandSender> context, final String input) {
        if (context.sender() instanceof Player player) {
            SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
            return sp.getIgnoreList().stream().map(SysteraProtos.PlayerIdentity::getName);
        }
        return Stream.of();
    }

    @Suggestions("on_off")
    public Stream<String> onOff(final CommandContext<CommandSender> context, final String input) {
        return Stream.of("on", "off");
    }
}
