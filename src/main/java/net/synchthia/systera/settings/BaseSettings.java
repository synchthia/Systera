package net.synchthia.systera.settings;

import org.bukkit.entity.Player;

public abstract class BaseSettings {
    private boolean value;

    public BaseSettings(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    public final void setValue(Player player, boolean value) {
        this.value = value;
        whenUpdate(player, value);
    }

    protected void whenUpdate(Player player, boolean value) {
    }
}
