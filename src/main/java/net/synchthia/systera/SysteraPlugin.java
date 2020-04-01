package net.synchthia.systera;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SysteraPlugin extends JavaPlugin {
    @Getter
    private static SysteraPlugin instance;

    @Override
    public void onEnable() {
        try {
            this.getLogger().log(Level.INFO, "Enabled: " + this.getName());
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Exception threw while onEnable: ", e);
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "Disabled: " + this.getName());
    }
}
