package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.util.DateUtil;
import org.bukkit.command.CommandSender;

import java.util.Date;
import java.util.logging.Level;

@RequiredArgsConstructor
public class SeenCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("seen")
    @CommandPermission("systera.command.seen")
    @Description("Show information of player")
    @CommandCompletion("@players")
    public void onSeen(CommandSender sender, String target) {
        plugin.getApiClient().fetchPlayerProfileByName(target).whenComplete((result, throwable) -> {
            if (throwable != null) {
                // TODO: Handle by Status code...
                if (throwable.toString().contains("record not found")) {
                    I18n.sendMessage(sender, "player.error.not_found");
                    return;
                }
                sender.sendMessage(Component.text("Failed lookup player: " + target).color(NamedTextColor.RED));
                sender.sendMessage(Component.text(throwable.toString()).color(NamedTextColor.RED));

                plugin.getLogger().log(Level.WARNING, "Failed lookup player", throwable);
            }

            SysteraProtos.PlayerEntry entry = result.getEntry();
            Date lastSeen = DateUtil.epochToDate(entry.getLastLogin() / 1000L);

            // Offline
            if (entry.getCurrentServer().equals("")) {
                I18n.sendMessage(sender, "player.seen.offline", Placeholder.unparsed("_player_name_", target), Placeholder.unparsed("_last_seen_", lastSeen.toString()));
                return;
            }

            // Online / Vanish
            if (entry.getSettings().getVanish()) {
                if (sender.hasPermission("systera.vanish")) {
                    I18n.sendMessage(sender, "player.seen.vanish", Placeholder.unparsed("_player_name_", target), Placeholder.unparsed("_server_name_", entry.getCurrentServer()), Placeholder.unparsed("_last_seen_", lastSeen.toString()));
                } else {
                    I18n.sendMessage(sender, "player.seen.offline", Placeholder.unparsed("_player_name_", target), Placeholder.unparsed("_last_seen_", lastSeen.toString()));
                }
            } else {
                I18n.sendMessage(sender, "player.seen.online", Placeholder.unparsed("_player_name_", target), Placeholder.unparsed("_server_name_", entry.getCurrentServer()), Placeholder.unparsed("_last_seen_", lastSeen.toString()));
            }
        });
    }
}
