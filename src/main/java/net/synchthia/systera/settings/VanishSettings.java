package net.synchthia.systera.settings;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

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

        player.setSleepingIgnored(value);
        player.setCanPickupItems(!value);
        player.setSilent(value);

        if (value) {
            player.setMetadata("vanished", new FixedMetadataValue(plugin, true));
            applyVanishEffect(player, true);

            plugin.getServer().getOnlinePlayers().stream()
                    .filter(p -> !p.hasPermission(this.getPermission()))
                    .forEach(p -> {
                        p.hidePlayer(plugin, player);
                    });
        } else {
            player.setMetadata("vanished", new FixedMetadataValue(plugin, false));
            applyVanishEffect(player, false);

            plugin.getServer().getOnlinePlayers()
                    .forEach(p -> {
                        p.showPlayer(plugin, player);
                    });
        }
    }

    public void applyVanishEffect(Player player, boolean value) {
        player.setInvisible(value);
        player.setGlowing(value);
    }
}
