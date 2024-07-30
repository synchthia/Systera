package net.synchthia.systera;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {
    private Configuration config;

    public PluginConfig(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public String getTabListHeader() {
        return this.config.getString("tabList.header", "");
    }

    public String getTabListFooter() {
        return this.config.getString("tabList.footer", "");
    }
}
