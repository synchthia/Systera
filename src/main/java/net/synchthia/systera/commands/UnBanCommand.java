package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor
public class UnBanCommand {
    private final SysteraPlugin plugin;

    @Command("unban|pardon <target>")
    @Permission("systera.command.unban")
    @CommandDescription("Unban the specified player")
    public void onUnBan(CommandSender sender, @Argument(value = "target", suggestions = "players") String target) {
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
