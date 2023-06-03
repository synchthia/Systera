package net.synchthia.systera.i18n;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

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

    public static void broadcastMessage(@NonNull String key, TagResolver... resolvers) {
        Bukkit.getOnlinePlayers().forEach(player -> sendMessage(player, key, resolvers));
        sendMessage(Bukkit.getConsoleSender(), key, resolvers);
    }

    public static Locale getLanguage(@NonNull CommandSender sender) {
        return sender instanceof Player ? ((Player) sender).locale() : null;
    }

    public static Component getComponent(CommandSender sender, @NonNull String key, TagResolver... resolvers) {
        return getI18nManager().getComponent(sender == null ? getI18nManager().getDefaultLanguage() : getLanguage(sender), key, resolvers);
    }

    public static void sendMessage(@NonNull CommandSender sender, @NonNull String key, TagResolver... resolvers) {
        sender.sendMessage(getComponent(sender, key, resolvers));
    }
}
