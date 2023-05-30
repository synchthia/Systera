package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class DispatchCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("dispatch")
    @CommandPermission("systera.command.dispatch")
    @Description("Dispatch commands for the specified or all servers")
//    @CommandCompletion("")
    public void onDispatch(CommandSender sender, String target, String command) {
        String message = StringUtil.coloring(String.format("&aDispatched: %s >> &6%s", target, command));

        sender.sendMessage(message);
        plugin.getApiClient().dispatch(target, command).whenComplete((result, throwable) -> {
            if (throwable != null) {
                sender.sendMessage(ChatColor.RED + "Failed dispatch command: ", throwable.getMessage());
                return;
            }
            sender.sendMessage(ChatColor.GREEN + "Dispatch success");
        });
    }
}
