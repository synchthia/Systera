package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class PunishCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("warn")
    @CommandPermission("systera.command.punishment")
    @CommandCompletion("@players @punish_reason")
    @Description("Warning Command")
    public void onWarn(CommandSender sender, String target, String reason) {
        plugin.getPunishAPI().punish(false, SysteraProtos.PunishLevel.WARN, sender, target, reason, 0L);
    }

    @CommandAlias("kick")
    @CommandPermission("systera.command.punishment")
    @CommandCompletion("@players @punish_reason")
    @Description("Kick Command")
    public void onKick(CommandSender sender, String target, String reason) {
        plugin.getPunishAPI().punish(false, SysteraProtos.PunishLevel.KICK, sender, target, reason, 0L);
    }

    @CommandAlias("tempban|tban|punish")
    @CommandPermission("systera.command.punishment")
    @CommandCompletion("@players @punish_reason")
    @Description("Temporary BAN Command")
    public void onTempBan(CommandSender sender, String target, String reason) {
//        String expireDate = args.hasFlag('t') ? args.getFlag('t') : "7d";
        String expireDate = "7d";

        try {
            Long expire = DateUtil.getEpochMilliTime() + DateUtil.parseDateString(expireDate);
            plugin.getPunishAPI().punish(false, SysteraProtos.PunishLevel.TEMPBAN, sender, target, reason, expire);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid Expire Date!");
        }
    }

    @CommandAlias("ban|permban|pban|ppunish")
    @CommandPermission("systera.command.punishment")
    @CommandCompletion("@players @punish_reason")
    @Description("Permanently BAN Command")
    public void onPermBan(CommandSender sender, String target, String reason) {
        plugin.getPunishAPI().punish(true, SysteraProtos.PunishLevel.PERMBAN, sender, target, reason, 0L);
    }
}
