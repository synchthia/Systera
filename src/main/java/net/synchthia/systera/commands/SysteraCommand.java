package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor
public class SysteraCommand {
    private final SysteraPlugin plugin;

    @Command("systera")
    @Permission("systera.command.systera")
    @CommandDescription("Systera command")
    public void onSystera(CommandSender sender) {

    }

    @Command("systera reload")
    @Permission("systera.command.systera")
    @CommandDescription("Systera command")
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
