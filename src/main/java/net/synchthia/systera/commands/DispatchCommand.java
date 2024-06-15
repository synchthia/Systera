package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor
public class DispatchCommand {
    private final SysteraPlugin plugin;

    @Command("dispatch <target> <command>")
    @Permission("systera.command.dispatch")
    @CommandDescription("Dispatch commands for the specified or all servers")
    public void onDispatch(CommandSender sender, @Argument("target") String target, @Argument("command") String command) {
        sender.sendRichMessage(String.format("<green>Dispatched: %s >> </green><gold>%s</gold>", target, command));

        plugin.getApiClient().dispatch(target, command).whenComplete((result, throwable) -> {
            if (throwable != null) {
                sender.sendRichMessage("<red>Failed dispatch command: </red>");
                sender.sendMessage(Component.text(throwable.getMessage()));
                return;
            }
            sender.sendRichMessage("<green>Dispatch success</green>");
        });
    }
}
