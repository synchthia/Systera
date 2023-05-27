package net.synchthia.systera.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.settings.BaseSettings;
import net.synchthia.systera.settings.Settings;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SettingsCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("settings|setting|set")
    @CommandPermission("systera.command.settings")
    @CommandCompletion("@player_settings on|off")
    @Description("Show / Set player settings")
    public void onSettings(Player player, @Optional String name, @Optional String value) {
        if (plugin.getPlayerStore().get(player.getUniqueId()) == null) {
            player.sendMessage(I18n.get(player, "player.error.local_profile"));
            return;
        }

        Settings settings = plugin.getPlayerStore().get(player.getUniqueId()).getSettings();

        // Show all settings
        if (name == null && value == null) {
            I18n.sendMessage(player, "player.settings.header");
            settings.getSettings().forEach((k, v) -> {
                if (v.hasPermission(player)) {
                    showStatus(player, k, v.getValue());
                }
            });
            I18n.sendMessage(player, "player.settings.footer");

            return;
        }

        if (name != null) {
            BaseSettings k = settings.getSettings().get(name.toLowerCase());
            if (!settings.getSettings().containsKey(name.toLowerCase()) || !k.hasPermission(player)) {
                I18n.sendMessage(player, "player.settings.error.not_found", name.toLowerCase());
                return;
            }

            // Get current value
            if (value == null) {
                showStatus(player, name.toLowerCase(), k.getValue());
                return;
            }

            showStatus(player, name.toLowerCase(), parseValue(value));
            k.setValue(player, parseValue(value));

            // Sync to API
            SysteraProtos.PlayerSettings proto = settings.toProto();
            plugin.getApiClient().setPlayerSettings(player.getUniqueId(), proto).whenComplete(((r, th) -> {
                if (th != null) {
                    I18n.sendMessage(player, "player.settings.error.sync_failed");
                    th.printStackTrace();
                }
            }));
        }
    }

    private void showStatus(Player player, String key, boolean value) {
        String status = StringUtil.coloring("&cOFF");
        if (value) {
            status = StringUtil.coloring("&aON");
        }

        I18n.sendMessage(player, "player.settings.entry", key, status);
    }

    private boolean parseValue(String value) {
        return value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true");
    }
}
