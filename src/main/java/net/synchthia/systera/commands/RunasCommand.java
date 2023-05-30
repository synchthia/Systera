package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class RunasCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("runas|sudo")
    @CommandPermission("systera.command.runas")
    @Description("Make another user perform a command")
    @CommandCompletion("@all_and_players")
    public void onRunas(CommandSender sender, String target, String command) {
        if (target.equals("*")) {
            sender.sendMessage(StringUtil.coloring("&aRun as &6everyone&a: &7") + command);
            plugin.getServer().getOnlinePlayers().forEach(player -> player.performCommand(command));
        } else {
            Player player = plugin.getServer().getPlayer(target);
            if (player == null) {
                I18n.sendMessage(sender, "player.error.offline", target);
                return;
            }

            sender.sendMessage(StringUtil.coloring("&aRun as &6" + player.getName() + "&a: &7") + command);
            player.performCommand(command);
        }
    }
}
