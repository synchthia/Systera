package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.player.SysteraPlayer;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor
public class IgnoreCommand {
    private final SysteraPlugin plugin;

    @Command("ignore")
    @Permission("systera.command.ignore")
    @CommandDescription("Block chat & private messages from the specified player")
    public void onDefault(Player sender) {
        sender.sendRichMessage("<gold>/ignore add <ID></gold>");
        sender.sendRichMessage("<gold>/ignore remove <ID></gold>");
        sender.sendRichMessage("<gold>/ignore list</gold>");
    }

    @Command("ignore add <target>")
    @Permission("systera.command.ignore")
    @CommandDescription("Block chat & private messages from the specified player")
    public void onAdd(Player sender, @Argument(value = "target", suggestions = "players") String target) {
        Player targetPlayer = plugin.getServer().getPlayer(target);
        SysteraPlayer sp = plugin.getPlayerStore().get(sender.getUniqueId());

        SysteraProtos.PlayerIdentity targetIdentity;

        // if target is online?
        if (targetPlayer != null) {
            if (targetPlayer.getName().equals(sender.getName())) {
                I18n.sendMessage(sender, "chat.error.cant_mute_myself");
                return;
            }

            targetIdentity = APIClient.buildPlayerIdentity(
                    targetPlayer.getUniqueId(),
                    targetPlayer.getName()
            );
        } else {
            targetIdentity = APIClient.buildPlayerIdentity(
                    null,
                    target
            );
        }

        plugin.getPlayerStore().get(sender.getUniqueId()).ignorePlayer(targetIdentity).whenComplete((result, throwable) -> {
            if (throwable != null) {
                I18n.sendMessage(sender, "chat.error.ignore", Placeholder.unparsed("_player_name_", target), Placeholder.unparsed("_message_", throwable.toString()));
                return;
            }

            if (result.getResult().equals(SysteraProtos.CallResult.NOT_FOUND)) {
                I18n.sendMessage(sender, "player.error.not_found");
                return;
            }

            if (result.getResult().equals(SysteraProtos.CallResult.DUPLICATED)) {
                I18n.sendMessage(sender, "chat.error.already_ignored", Placeholder.unparsed("_player_name_", target));
                return;
            }

            I18n.sendMessage(sender, "chat.ignore.success", Placeholder.unparsed("_player_name_", result.getIdentity().getName()));

            plugin.getServer().getScheduler().runTask(plugin, () -> sp.getIgnoreList().add(result.getIdentity()));
        });
    }

    @Command("ignore remove <target>")
    @Permission("systera.command.ignore")
    @CommandDescription("Block chat & private messages from the specified player")
    public void onRemove(Player sender, @Argument(value = "target", suggestions = "ignored_players") String target) {
        Player targetPlayer = plugin.getServer().getPlayer(target);
        SysteraPlayer sp = plugin.getPlayerStore().get(sender.getUniqueId());

        if (sp.getIgnoreList().stream().noneMatch(pi -> pi.getName().equalsIgnoreCase(target))) {
            I18n.sendMessage(sender, "chat.error.not_ignored", Placeholder.unparsed("_player_name_", target));
            return;
        }

        SysteraProtos.PlayerIdentity targetIdentity;

        // if target is online?
        if (targetPlayer != null) {
            targetIdentity = APIClient.buildPlayerIdentity(
                    targetPlayer.getUniqueId(),
                    targetPlayer.getName()
            );
        } else {
            targetIdentity = APIClient.buildPlayerIdentity(
                    null,
                    target
            );
        }

        plugin.getPlayerStore().get(sender.getUniqueId()).unIgnorePlayer(targetIdentity).whenComplete((result, throwable) -> {
            if (throwable != null) {
                I18n.sendMessage(sender, "chat.error.unignore", Placeholder.unparsed("_player_name_", target), Placeholder.unparsed("_message_", throwable.toString()));
                return;
            }

            if (result.getResult().equals(SysteraProtos.CallResult.NOT_FOUND)) {
                I18n.sendMessage(sender, "player.error.not_found");
                return;
            }

            if (result.getResult().equals(SysteraProtos.CallResult.DUPLICATED)) {
                I18n.sendMessage(sender, "chat.error.already_ignored", Placeholder.unparsed("_player_name_", target));
                return;
            }

            I18n.sendMessage(sender, "chat.unignore.success", Placeholder.unparsed("_player_name_", result.getIdentity().getName()));

            plugin.getServer().getScheduler().runTask(plugin, () -> sp.getIgnoreList().remove(result.getIdentity()));
        });
    }

    @Command("ignore list")
    @Permission("systera.command.ignore")
    @CommandDescription("Block chat & private messages from the specified player")
    public void onList(Player sender) {
        SysteraPlayer sp = plugin.getPlayerStore().get(sender.getUniqueId());

        if (sp.getIgnoreList().size() == 0) {
            I18n.sendMessage(sender, "chat.error.ignore_empty");
            return;
        }

        I18n.sendMessage(sender, "chat.ignore.title");
        sender.sendMessage(Component.text(String.join(", ", sp.getIgnoreList().stream().map(SysteraProtos.PlayerIdentity::getName).toList())));
    }
}
