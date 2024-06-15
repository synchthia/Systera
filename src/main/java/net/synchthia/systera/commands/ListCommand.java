package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.SysteraPlayer;
import net.synchthia.systera.settings.VanishSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@RequiredArgsConstructor
public class ListCommand {
    private final SysteraPlugin plugin;

    @Command("list|who")
    @Permission("systera.command.list")
    @CommandDescription("List online players")
    public void onList(CommandSender sender) {
        List<Component> players = plugin.getServer().getOnlinePlayers().stream().filter(p -> {
            VanishSettings vanish = plugin.getPlayerStore().get(p.getUniqueId()).getSettings().getVanish();
            if (sender.hasPermission(vanish.getPermission())) {
                return true;
            } else {
                Player senderPlayer = plugin.getServer().getPlayer(sender.getName());
                return !vanish.getValue() || (senderPlayer != null && senderPlayer.canSee(p));
            }
        }).map(p -> {
            SysteraPlayer sp = plugin.getPlayerStore().get(p.getUniqueId());
            VanishSettings vanish = sp.getSettings().getVanish();
            if (vanish.getValue()) {
                return Component.text("[Vanish]").color(NamedTextColor.YELLOW)
                        .append(p.displayName());
            } else {
                return sp.getPrefix().append(p.displayName());
            }
        }).toList();

        sender.sendRichMessage(String.format("<aqua>Online</aqua> <gold>(%d/%d)</gold>", players.size(), plugin.getServer().getMaxPlayers()));
        sender.sendMessage(Component.join(JoinConfiguration.commas(true), players));
    }
}
