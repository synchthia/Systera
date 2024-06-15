package net.synchthia.systera.commands;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.player.SysteraPlayer;
import net.synchthia.systera.settings.BaseSettings;
import net.synchthia.systera.settings.Settings;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor
public class SettingsCommand {
    private final SysteraPlugin plugin;

    @Command("settings|setting|set [name] [value]")
    @Permission("systera.command.settings")
    @CommandDescription("Show / Set player settings")
    public void onSettings(Player player, @Argument(value = "name", suggestions = "player_settings") String name, @Argument(value = "value", suggestions = "on_off") String value) {
        if (plugin.getPlayerStore().get(player.getUniqueId()) == null) {
            I18n.sendMessage(player, "player.error.local_profile");
            return;
        }

        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
        Settings settings = sp.getSettings();

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
                I18n.sendMessage(player, "player.settings.error.not_found", Placeholder.unparsed("_setting_name_", name.toLowerCase()));
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
            sp.syncSettings().whenComplete((res, throwable) -> {
                if (throwable != null) {
                    I18n.sendMessage(player, "player.settings.error.sync_failed");
                }
            });
        }
    }

    @Command("vanish|v")
    @Permission("systera.vanish")
    @CommandDescription("Hidden from player")
    public void onVanish(Player sender) {
        SysteraPlayer sp = plugin.getPlayerStore().get(sender.getUniqueId());
        boolean value = !sp.getSettings().getVanish().getValue();
        sp.getSettings().getVanish().setValue(sender, value);

        showStatus(sender, "vanish", value);

        // Sync to API
        sp.syncSettings().whenComplete((res, throwable) -> {
            if (throwable != null) {
                I18n.sendMessage(sender, "player.settings.error.sync_failed");
            }
        });
    }

    private void showStatus(Player player, String key, boolean value) {
        Component status = value ?
                Component.text("ON").color(NamedTextColor.GREEN) :
                Component.text("OFF").color(NamedTextColor.RED);

        I18n.sendMessage(player, "player.settings.entry", Placeholder.unparsed("_setting_name_", key), Placeholder.component("_setting_value_", status));
    }

    private boolean parseValue(String value) {
        return value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true");
    }
}
