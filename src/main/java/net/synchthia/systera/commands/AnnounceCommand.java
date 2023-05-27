package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.announce.Announce;
import org.bukkit.Sound;
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

    @CommandAlias("chime")
    @CommandPermission("systera.command.announce")
    @CommandCompletion("on|off")
    @Description("Announce chime")
    public void onChime(CommandSender sender, String status) {
        if (status.equals("on")) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1);
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1);
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1);

                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.25f), 10L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.25f), 10L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.25f), 10L);

                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.5f), 20L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.5f), 20L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.5f), 20L);

                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0f), 30L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0f), 30L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0f), 30L);
            }
        } else {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0f), 10L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0f), 10L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0f), 10L);

                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.5f), 20L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.5f), 20L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.5f), 20L);

                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.25f), 30L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.25f), 30L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.25f), 30L);

                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1f), 40L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1f), 40L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1), 40L);
            }
        }
    }
}
