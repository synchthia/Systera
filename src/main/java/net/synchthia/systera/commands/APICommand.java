package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.group.Group;
import net.synchthia.systera.player.SysteraPlayer;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
}
