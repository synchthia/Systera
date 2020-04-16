package net.synchthia.systera.util;

import org.bukkit.ChatColor;

public class StringUtil {
    public static String coloring(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
