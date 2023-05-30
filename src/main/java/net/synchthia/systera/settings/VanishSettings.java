package net.synchthia.systera.settings;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
        player.setMetadata("vanished", new FixedMetadataValue(plugin, value));

        if (value) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 1, false));

            plugin.getServer().getOnlinePlayers().stream()
                    .filter(p -> !p.hasPermission(this.getPermission()))
                    .forEach(p -> {
                        p.hidePlayer(plugin, player);
                    });
        } else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.GLOWING);

            plugin.getServer().getOnlinePlayers()
                    .forEach(p -> {
                        p.showPlayer(plugin, player);
                    });
        }
    }
}
