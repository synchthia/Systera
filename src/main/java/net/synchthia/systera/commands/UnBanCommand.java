package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class UnBanCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("unban|pardon")
    @CommandPermission("systera.command.unban")
    @Description("Unban the specified player")
    @CommandCompletion("@players")
    public void onUnBan(CommandSender sender, String target) {
        plugin.getPunishAPI().unBan(target).whenComplete((result, throwable) -> {
            if (throwable != null) {
                sender.sendRichMessage("<red>Failed unban player:</red>");
                sender.sendMessage(Component.text(throwable.toString()));
                return;
            }

            I18n.sendMessage(sender, "punishments.unban", Placeholder.unparsed("_player_name_", target));
        });
    }
}
