package net.synchthia.systera.settings;

import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.SysteraPlayer;
import org.bukkit.entity.Player;

public class VanishSettings extends BaseSettings {
    public VanishSettings(boolean value) {
        super(value);
    }

    @Override
    protected void whenUpdate(Player player, boolean value) {
        SysteraPlugin plugin = SysteraPlugin.getInstance();

        SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
        if (sp.getSettings().getVanish().getValue()) {
            plugin.getServer().getOnlinePlayers().stream()
                    .filter(p -> !p.hasPermission("systera.vanish"))
                    .forEach(p -> p.hidePlayer(plugin, player));
        } else {
            plugin.getServer().getOnlinePlayers()
                    .forEach(p -> p.showPlayer(plugin, player));
        }
    }
}
