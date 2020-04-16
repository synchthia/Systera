package net.synchthia.systera.i18n;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author misterT2525
 */
public final class I18n {

    @Getter
    @Setter
    private static I18nManager i18nManager;

    private I18n() {
        throw new RuntimeException("Non instantiable class");
    }

    public static void broadcastMessage(@NonNull String key, Object... extra) {
        Bukkit.getOnlinePlayers().forEach(player -> sendMessage(player, key, extra));
        sendMessage(Bukkit.getConsoleSender(), key, extra);
    }

    public static String[] get(@NonNull CommandSender sender, @NonNull String key, Object... extra) {
        return getI18nManager().get(getLanguage(sender), key, extra);
    }

    public static String getLanguage(@NonNull CommandSender sender) {
        return sender instanceof Player ? ((Player) sender).getLocale() : null;
    }

    public static String getString(@NonNull CommandSender sender, @NonNull String key, Object... extra) {
        return getI18nManager().getString(getLanguage(sender), key, extra);
    }

    public static void sendMessage(@NonNull CommandSender sender, @NonNull String key, Object... extra) {
        sendMessage(sender, get(sender, key, extra));
    }

    private static void sendMessage(@NonNull CommandSender sender, String[] messages) {
        sender.sendMessage(messages);
    }
}
