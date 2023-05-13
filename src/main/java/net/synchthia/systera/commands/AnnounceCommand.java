package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.announce.Announce;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class AnnounceCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("announce")
    @CommandPermission("systera.command.announce")
    @Description("Announce in Bossbar")
    public void onAnnounce(CommandSender sender, String message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Announce task = new Announce(player, message);
            task.sendAnnounce();
        }
    }
}
