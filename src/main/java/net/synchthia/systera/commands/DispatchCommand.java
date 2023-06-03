package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class DispatchCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("dispatch")
    @CommandPermission("systera.command.dispatch")
    @Description("Dispatch commands for the specified or all servers")
//    @CommandCompletion("")
    public void onDispatch(CommandSender sender, String target, String command) {
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
