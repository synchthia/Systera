package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.command.CommandSender;

@CommandAlias("systera")
@CommandPermission("systera.command.systera")
@Description("Systera command")
@RequiredArgsConstructor
public class SysteraCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    public void onSystera(CommandSender sender) {

    }

    @Subcommand("reload")
    @CommandPermission("systera.command.reload")
    public void onReload(CommandSender sender) {
        // Disconnect
        try {
            plugin.getApiClient().shutdown();
            plugin.getRedisClient().disconnect();
        } catch (InterruptedException e) {
            sender.sendRichMessage("<red>Failed shutdown api / stream</red>");
            throw new RuntimeException(e);
        }

        // Connect...
        try {
            plugin.registerAPI();
            plugin.registerRedis();
        } catch (InterruptedException e) {
            sender.sendRichMessage("<red>Failed initialize api / stream</red>");
            throw new RuntimeException(e);
        }
        
        sender.sendRichMessage("<green>Plugin Reloaded.</green>");
    }
}
