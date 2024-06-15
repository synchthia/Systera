package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.DateUtil;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor
public class PunishCommand {
    private final SysteraPlugin plugin;

    @Command("warn <target> <reason>")
    @Permission("systera.command.punishment")
    @CommandDescription("Warning Command")
    public void onWarn(CommandSender sender, @Argument(value = "target", suggestions = "players") String target, @Argument(value = "reason", suggestions = "punish_reason") String reason) {
        plugin.getPunishAPI().punish(false, SysteraProtos.PunishLevel.WARN, sender, target, reason, 0L);
    }

    @Command("kick <target> <reason>")
    @Permission("systera.command.punishment")
    @CommandDescription("Kick Command")
    public void onKick(CommandSender sender, @Argument(value = "target", suggestions = "players") String target, @Argument(value = "reason", suggestions = "punish_reason") String reason) {
        plugin.getPunishAPI().punish(false, SysteraProtos.PunishLevel.KICK, sender, target, reason, 0L);
    }

    @Command("tempban|tban|punish <target> <reason>")
    @Permission("systera.command.punishment")
    @CommandDescription("Temporary BAN Command")
    public void onTempBan(CommandSender sender, @Argument(value = "target", suggestions = "players") String target, @Argument(value = "reason", suggestions = "punish_reason") String reason) {
//        String expireDate = args.hasFlag('t') ? args.getFlag('t') : "7d";
        String expireDate = "7d";

        try {
            Long expire = DateUtil.getEpochMilliTime() + DateUtil.parseDateString(expireDate);
            plugin.getPunishAPI().punish(false, SysteraProtos.PunishLevel.TEMPBAN, sender, target, reason, expire);
        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Invalid expire date!</red>");
        }
    }

    @Command("ban|permban|pban|ppunish <target> <reason>")
    @Permission("systera.command.punishment")
    @CommandDescription("Permanently BAN Command")
    public void onPermBan(CommandSender sender, @Argument(value = "target", suggestions = "players") String target, @Argument(value = "reason", suggestions = "punish_reason") String reason) {
        plugin.getPunishAPI().punish(true, SysteraProtos.PunishLevel.PERMBAN, sender, target, reason, 0L);
    }
}
