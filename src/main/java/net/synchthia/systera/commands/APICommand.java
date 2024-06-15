package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.group.Group;
import net.synchthia.systera.player.SysteraPlayer;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class APICommand {
    private final SysteraPlugin plugin;

    @Command("api whoami")
    @Permission("systera.command.api")
    public void onWhoami(Player player) {
        if (!player.getAddress().isUnresolved()) {
            player.sendMessage(player.getAddress().getAddress().getHostAddress());
        }
    }

    @Command("api playerstore")
    @Permission("systera.command.api")
    public void onPlayerStore(CommandSender sender) {
        int storeSize = this.plugin.getPlayerStore().size();
        sender.sendMessage("Store Size: " + storeSize);
    }

    @Command("api groupstore")
    @Permission("systera.command.api")
    public void onGroupStore(CommandSender sender) {
        SysteraPlayer sp = plugin.getPlayerStore().get(plugin.getServer().getPlayer(sender.getName()).getUniqueId());
        int size = this.plugin.getGroupStore().size();
        sender.sendMessage("Store Size: " + size);
        Group defaultGroup = this.plugin.getGroupStore().get("default");
        sender.sendMessage("Default: " + defaultGroup.getName());
        sender.sendMessage("Prefix: " + defaultGroup.getPrefix());
        sender.sendMessage(sp.getPrefix());
        sender.sendMessage("---");
        sender.sendMessage("Groups:");
        sp.getGroups().forEach((g) -> {
            sender.sendMessage("-> " + g);
        });
    }

    @Command("api perms")
    @Permission("systera.command.api")
    public void onPerms(Player player) {
        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
        player.sendMessage("# Current Permissions (Local):");
        sp.getAttachment().getPermissions().forEach((perms, status) -> {
            if (status) {
                player.sendMessage(StringUtil.coloring(String.format("&a+ &f%s", perms)));
            } else {
                player.sendMessage(StringUtil.coloring(String.format("&c- &f%s", perms)));
            }
        });
    }

    @Command("api removeattachments")
    @Permission("systera.command.api")
    public void onRemoveAttachments(Player player) {
        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
//        sp.removeAttachments();
        sp.refreshAttachment();
    }

    @Command("api player <target>")
    @Permission("systera.command.api")
    public void onPlayer(CommandSender sender, @Argument(value = "target", suggestions = "players") String target) {
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(target);
        if (offlinePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        sender.sendMessage("Name: " + offlinePlayer.getName());
        sender.sendMessage("UUID: " + offlinePlayer.getUniqueId());
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            sender.sendMessage("** Player is null **");
            return;
        }

        sender.sendMessage(player.hasMetadata("vanished") ? "vanish metadata" : "non vanish metadata");

        sender.sendMessage("Name: " + player.getName());
        sender.sendMessage("DisplayName: " + player.getDisplayName());
        sender.sendMessage("Locale: " + player.getLocale());
        sender.sendMessage(Component.text("Locale: " + player.locale()));

        sender.sendMessage(" --- ");
        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
        sender.sendMessage("Ignores (Local): ");
        sender.sendMessage(sp.getIgnoreList().toString());
        sender.sendMessage("Client: " + sp.getPlayer().getClientBrandName());

    }

    @Command("api getpi <target>")
    @Permission("systera.command.api")
    public void onGetPI(CommandSender sender, @Argument(value = "target", suggestions = "players") String target) {
        try {
            SysteraProtos.GetPlayerIdentityByNameResponse pi = plugin.getApiClient().getPlayerIdentity(target).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {

        }
    }
}
