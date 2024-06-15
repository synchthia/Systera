package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor
public class RunasCommand {
    private final SysteraPlugin plugin;

    @Command("runas|sudo <target> <command>")
    @Permission("systera.command.runas")
    @CommandDescription("Make another user perform a command")
    public void onRunas(CommandSender sender, @Argument(value = "target", suggestions = "all_and_players") String target, String command) {
        if (target.equals("*")) {
            sender.sendMessage(
                    MiniMessage.miniMessage().deserialize("<green>Run as </green><gold>everyone</gold><green>: </green>")
                            .append(Component.text(command).color(NamedTextColor.GRAY))
            );
            plugin.getServer().getOnlinePlayers().forEach(player -> player.performCommand(command));
        } else {
            Player player = plugin.getServer().getPlayer(target);
            if (player == null) {
                I18n.sendMessage(sender, "player.error.offline", Placeholder.unparsed("_player_name_", target));
                return;
            }

            sender.sendMessage(
                    MiniMessage.miniMessage().deserialize("<green>Run as </green><gold>" + player.getName() + "</gold><green>: </green>")
                            .append(Component.text(command).color(NamedTextColor.GRAY))
            );
            player.performCommand(command);
        }
    }
}
