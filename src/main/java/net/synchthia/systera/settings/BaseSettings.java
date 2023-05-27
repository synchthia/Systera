package net.synchthia.systera.settings;

import org.bukkit.entity.Player;

public abstract class BaseSettings {
    private boolean value;
    private String permission;

    public BaseSettings(boolean value) {
        this.value = value;
        this.permission = "systera.command.settings";
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

    public final void setPermission(String perm) {
        this.permission = perm;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(this.getPermission());
    }
}
