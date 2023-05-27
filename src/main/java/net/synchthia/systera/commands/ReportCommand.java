package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class ReportCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("report|modreq|sos|helpop")
    @CommandPermission("systera.command.report")
    @CommandCompletion("@players @punish_reason")
    @Description("Report to Staff")
    public void onReport(CommandSender sender, String target, String message) {
        Player targetPlayer = plugin.getServer().getPlayer(target);
        if (targetPlayer == null) {
            I18n.sendMessage(sender, "player.error.not_found");
            return;
        }

        UUID toUUID = targetPlayer.getUniqueId();
        String toName = targetPlayer.getName();

        I18n.sendMessage(sender, "report.thanks");

        if (plugin.getServer().getPlayer(sender.getName()) != null) {
            plugin.getApiClient().report(Objects.requireNonNull(plugin.getServer().getPlayer(sender.getName())).getUniqueId(), sender.getName(), toUUID, toName, SysteraPlugin.getServerId(), message);
        } else {
            plugin.getApiClient().report(null, sender.getName(), toUUID, toName, SysteraPlugin.getServerId(), message);
        }
    }
}
