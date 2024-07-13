package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class ReportCommand {
    private final SysteraPlugin plugin;

    @Command("report|modreq|sos|helpop <target> <message>")
    @Permission("systera.command.report")
    @CommandDescription("Report to Staff")
    public void onReport(CommandSender sender, @Argument(value = "target", suggestions = "players") String target, @Argument(value = "message", suggestions = "punish_reason") @Greedy String message) {
        Player targetPlayer = plugin.getServer().getPlayer(target);

        UUID toUUID = targetPlayer != null ? targetPlayer.getUniqueId() : null;
        String toName = targetPlayer != null ? targetPlayer.getName() : target;

        I18n.sendMessage(sender, "report.thanks");

        if (plugin.getServer().getPlayer(sender.getName()) != null) {
            plugin.getApiClient().report(Objects.requireNonNull(plugin.getServer().getPlayer(sender.getName())).getUniqueId(), sender.getName(), toUUID, toName, SysteraPlugin.getServerId(), message);
        } else {
            plugin.getApiClient().report(null, sender.getName(), toUUID, toName, SysteraPlugin.getServerId(), message);
        }
    }
}
