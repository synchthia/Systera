package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
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
}
