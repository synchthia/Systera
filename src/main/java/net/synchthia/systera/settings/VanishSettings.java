package net.synchthia.systera.settings;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.entity.Player;

public class VanishSettings extends BaseSettings {
    public VanishSettings(boolean value) {
        super(value);
        this.setPermission("systera.vanish");
    }

    @Override
    protected void whenUpdate(Player player, boolean value) {
        vanish(player, value);
    }

    public void vanish(Player player, boolean value) {
        SysteraPlugin plugin = SysteraPlugin.getInstance();

        if (value) {
            plugin.getServer().getOnlinePlayers().stream()
                    .filter(p -> !p.hasPermission(this.getPermission()))
                    .forEach(p -> {
                        p.hidePlayer(plugin, player);
                        p.setSleepingIgnored(true);
                        p.setCanPickupItems(false);
                        p.setVisibleByDefault(true);
                        p.setSilent(true);
                    });
        } else {
            plugin.getServer().getOnlinePlayers()
                    .forEach(p -> {
                        p.showPlayer(plugin, player);
                        p.setSleepingIgnored(false);
                        p.setCanPickupItems(true);
                        p.setVisibleByDefault(false);
                        p.setSilent(false);
                    });
        }
    }
}
