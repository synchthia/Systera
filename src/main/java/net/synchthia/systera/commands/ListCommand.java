package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.settings.VanishSettings;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class ListCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("list|who")
    @CommandPermission("systera.command.list")
    @Description("List online players")
    public void onList(CommandSender sender) {
        List<String> players = plugin.getServer().getOnlinePlayers().stream().filter(p -> {
            VanishSettings vanish = plugin.getPlayerStore().get(p.getUniqueId()).getSettings().getVanish();
            if (sender.hasPermission(vanish.getPermission())) {
                return true;
            } else {
                Player senderPlayer = plugin.getServer().getPlayer(sender.getName());
                return !vanish.getValue() || (senderPlayer != null && senderPlayer.canSee(p));
            }
        }).map(p -> {
            VanishSettings vanish = plugin.getPlayerStore().get(p.getUniqueId()).getSettings().getVanish();
            if (vanish.getValue()) {
                return StringUtil.coloring("&e[Vanish]&r" + p.getDisplayName());
            } else {
                return p.getName();
            }
        }).toList();

        sender.sendMessage(StringUtil.coloring("&bOnline &6(" + players.size() + "/" + Bukkit.getMaxPlayers() + ")"));
        sender.sendMessage(String.join(", ", players));
    }
}
