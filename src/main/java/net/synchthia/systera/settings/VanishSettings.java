package net.synchthia.systera.settings;

import org.bukkit.entity.Player;

public class VanishSettings extends BaseSettings {
    public VanishSettings(boolean value) {
        super(value);
    }

    @Override
    protected void whenUpdate(Player player, boolean value) {
        // TODO: Implement vanish
    }
}
