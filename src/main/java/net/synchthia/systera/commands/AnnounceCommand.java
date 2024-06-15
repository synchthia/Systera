package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.announce.Announce;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor
public class AnnounceCommand {
    private final SysteraPlugin plugin;

    @Command("announce <message>")
    @Permission("systera.command.announce")
    @CommandDescription("Announce in Bossbar")
    public void onAnnounce(CommandSender sender, @Argument("message") @Greedy String message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Announce task = new Announce(player, message);
            task.sendAnnounce();
        }
    }

    @Command("chime <status>")
    @Permission("systera.command.announce")
    @CommandDescription("Announce chime")
    public void onChime(CommandSender sender, @Argument(value = "status", suggestions = "on_off") String status) {
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
