package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.RequiredArgsConstructor;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.group.Group;
import net.synchthia.systera.player.SysteraPlayer;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@CommandAlias("api")
@CommandPermission("systera.command.api")
@RequiredArgsConstructor
public class APICommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @Subcommand("whoami")
    public void onWhoami(Player player) {
        if (!player.getAddress().isUnresolved()) {
            player.sendMessage(player.getAddress().getAddress().getHostAddress());
        }
    }

    @Subcommand("playerstore")
    public void onPlayerStore(CommandSender sender) {
        int storeSize = this.plugin.getPlayerStore().size();
        sender.sendMessage("Store Size: " + storeSize);
    }

    @Subcommand("groupstore")
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

    @Subcommand("perms")
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

    @Subcommand("removeattachments")
    public void onRemoveAttachments(Player player) {
        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
//        sp.removeAttachments();
        sp.refreshAttachment();
    }

    @Subcommand("player")
    @CommandCompletion("@players")
    public void onPlayer(CommandSender sender, String target) {
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

        sender.sendMessage("Name: " + player.getName());
        sender.sendMessage("DisplayName: " + player.getDisplayName());
        sender.sendMessage("Locale: " + player.getLocale());

        sender.sendMessage(" --- ");
        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
        sender.sendMessage("Ignores (Local): ");
        sender.sendMessage(sp.getIgnoreList().toString());
        sender.sendMessage("Client: " + sp.getPlayer().getClientBrandName());

    }

    @Subcommand("getpi")
    @CommandCompletion("@players")
    public void onGetPI(CommandSender sender, String target) {
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
