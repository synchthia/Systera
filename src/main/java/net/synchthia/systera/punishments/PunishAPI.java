package net.synchthia.systera.punishments;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.util.BungeeUtil;
import net.synchthia.systera.util.DateUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class PunishAPI {
    private final SysteraPlugin plugin;

    public PunishAPI(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<SysteraProtos.GetPlayerPunishResponse> lookup(UUID playerUUID, SysteraProtos.PunishLevel filterLevel) {
        return plugin.getApiClient().getPlayerPunishment(playerUUID, filterLevel, false).whenComplete((value, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed lookup player", throwable);
            }
        });
    }

    public CompletableFuture<SysteraProtos.UnBanResponse> unBan(String target) {
        SysteraProtos.PlayerIdentity identity;
        Player player = plugin.getServer().getPlayer(target);

        // Online Player
        if (player != null) {
            identity = APIClient.buildPlayerIdentity(player.getUniqueId(), player.getName());
        } else {
            identity = APIClient.buildPlayerIdentity(null, target);
        }

        return plugin.getApiClient().unBanPlayer(identity).whenComplete((result, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed unban player", throwable);
            }
        });
    }

    public CompletableFuture<SysteraProtos.SetPlayerPunishResponse> punish(Boolean force, SysteraProtos.PunishLevel punishLevel, CommandSender sender, String toPlayerName, String reason, Long expire) {
        UUID fromPlayerUUID = null;
        String fromPlayerName = sender.getName();

        UUID toPlayerUUID = null;

        if (punishLevel.equals(SysteraProtos.PunishLevel.UNRECOGNIZED)) {
            punishLevel = SysteraProtos.PunishLevel.PERMBAN;
        }

        if (plugin.getServer().getPlayer(fromPlayerName) != null) {
            fromPlayerUUID = plugin.getServer().getPlayer(fromPlayerName).getUniqueId();
        }

        // target
        boolean remote;
        if (plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(toPlayerName))) {
            remote = false;
            toPlayerUUID = plugin.getServer().getPlayer(toPlayerName).getUniqueId();
        } else {
            remote = true;
        }

        SysteraProtos.PlayerIdentity from = APIClient.buildPlayerIdentity(fromPlayerUUID, fromPlayerName);
        SysteraProtos.PlayerIdentity to = APIClient.buildPlayerIdentity(toPlayerUUID, toPlayerName);

        // Finalize
        SysteraProtos.PunishLevel level = punishLevel;
        boolean rmt = remote;
        return plugin.getApiClient().setPlayerPunishment(remote, force, from, to, punishLevel, reason, expire).whenComplete((res, throwable) -> {
            if (throwable != null) {
                sender.sendRichMessage("<red>Failed punish player:</red>");
                sender.sendMessage(Component.text(throwable.toString()));
                plugin.getLogger().log(Level.WARNING, "Failed punish player", throwable);
                return;
            }

            if (!force && res.getNoProfile()) {
                I18n.sendMessage(sender, "punishments.error.no_profile", Placeholder.unparsed("_player_name_", toPlayerName));
                return;
            }

            if (res.getDuplicate()) {
                I18n.sendMessage(sender, "punishments.error.duplicate", Placeholder.unparsed("_player_name_", toPlayerName));
                return;
            }

            if (res.getCooldown()) {
                I18n.sendMessage(sender, "punishments.error.cooldown", Placeholder.unparsed("_player_name_", toPlayerName));
                return;
            }

            if (!force && level.getNumber() < SysteraProtos.PunishLevel.TEMPBAN.getNumber() && res.getOffline()) {
                I18n.sendMessage(sender, "player.error.offline", Placeholder.unparsed("_player_name_", toPlayerName));
                return;
            }

            // local execution
            if (!rmt) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    execution(plugin.getServer().getPlayer(toPlayerName).getUniqueId(), level, reason, expire);
                });
            }
        });
    }

    public void execution(UUID targetUUID, SysteraProtos.PunishLevel level, String reason, Long expire) {
        long unixDate = DateUtil.getEpochMilliTime();

        // Get Player
        Player targetPlayer = plugin.getServer().getPlayer(targetUUID);
        if (targetPlayer == null) {
            return;
        }

        if (level == SysteraProtos.PunishLevel.WARN) {
            targetPlayer.sendMessage(message(targetUUID, level, reason, unixDate, expire));
        } else {
            BungeeUtil.disconnect(plugin, targetPlayer, message(targetUUID, level, reason, unixDate, expire).toString());
        }
    }

    public void broadcast(SysteraProtos.PunishLevel level, String targetName, String reason) {
        I18n.broadcastMessage("punishments.notify",
                Placeholder.unparsed("_punish_level_", level.name()),
                Placeholder.unparsed("_player_name_", targetName),
                Placeholder.unparsed("_reason_", reason)
        );
    }

    public Component message(UUID targetUUID, SysteraProtos.PunishLevel level, String reason, Long unixDate, Long expire) {
        Date date = DateUtil.epochToDate(unixDate / 1000L);

        // Get Player
        Player targetPlayer = plugin.getServer().getPlayer(targetUUID);

        if (level == SysteraProtos.PunishLevel.WARN) {
            return I18n.getComponent(targetPlayer, "punishments.dialog.warn", Placeholder.unparsed("_reason_", reason));
        } else if (level == SysteraProtos.PunishLevel.KICK) {
            return I18n.getComponent(targetPlayer, "punishments.dialog.kick", Placeholder.unparsed("_reason_", reason));
        } else if (level == SysteraProtos.PunishLevel.TEMPBAN) {
            return I18n.getComponent(targetPlayer, "punishments.dialog.tempban", Placeholder.unparsed("_reason_", reason), Placeholder.unparsed("_date_", date.toString()), Placeholder.unparsed("_expire_", DateUtil.epochToDate(expire / 1000L).toString()));
        } else if (level == SysteraProtos.PunishLevel.PERMBAN) {
            return I18n.getComponent(targetPlayer, "punishments.dialog.permban", Placeholder.unparsed("_reason_", reason), Placeholder.unparsed("_date_", date.toString()));
        } else {
            return Component.text("You are disconnected from this server: " + Placeholder.unparsed("_reason_", reason));
        }
    }
}
