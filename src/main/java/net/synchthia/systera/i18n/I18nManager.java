package net.synchthia.systera.i18n;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import lombok.Cleanup;
import lombok.Data;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author misterT2525
 */
@Data
public class I18nManager {

    private static final Charset ENCODE = Charsets.UTF_8;
    private static final String EXTENSION = ".yml";
    private static final String DEFAULT_MESSAGES_FILENAME = toLocaleString(Locale.US) + EXTENSION;
    private static final String LANG_DIRNAME = "lang/";

    private final JavaPlugin plugin;
    private final Map<String, Configuration> languages = new HashMap<>();
    @NonNull
    private Configuration defaultMessages;
    private Locale defaultLanguage = Locale.US;

    public I18nManager(@NonNull JavaPlugin plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;

        loadDefaultMessages();
        loadLanguageFiles();
    }

    private String[] format(String[] base, Object... extra) {
        for (int i = 0; i < base.length; i++) {
            base[i] = MessageFormat.format(base[i], extra);
        }
        return base;
    }

    @SuppressWarnings("unchecked")
    public String[] get(Locale language, @NonNull String key, Object... extra) {
        language = language != null ? language : defaultLanguage;

        Object object = getLanguage(language).get(key);
        if (language != null && !language.equals(defaultLanguage) && object == null) {
            return get(defaultLanguage, key, extra);
        }
        if (language != null && language.equals(defaultLanguage) && object == null) {
            return null;
        }
        if (object instanceof String[]) {
            return format((String[]) object, extra);
        }
        if (object instanceof List) {
            return format(((List<Object>) object).stream().map(Object::toString).toArray(String[]::new), extra);
        }
        return format(object.toString().split("\n"), extra);
    }

    public Configuration getLanguage(Locale language) {
        Configuration configuration = null;
        if (language != null) {
            configuration = languages.get(toLocaleString(language));
        }
        if (configuration == null && defaultLanguage != null) {
            configuration = languages.get(toLocaleString(defaultLanguage));
        }
        if (configuration == null) {
            configuration = defaultMessages;
        }
        return configuration;
    }

    public Component getComponent(Locale language, @NonNull String key, TagResolver... resolvers) {
        String[] array = get(language, key);
        return array == null ? null : MiniMessage.miniMessage().deserialize(Joiner.on('\n').join(array), resolvers);
    }

    private void loadDefaultMessages() throws IOException, InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(new InputStreamReader(getPlugin().getResource(LANG_DIRNAME + DEFAULT_MESSAGES_FILENAME), ENCODE));
        defaultMessages = configuration;
    }

    private void loadLanguageFiles() throws IOException, InvalidConfigurationException {
        languages.clear();

        File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        if (!file.isFile()) {
            return;
        }
        @Cleanup
        JarFile jar = new JarFile(file);
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().startsWith(LANG_DIRNAME) || !entry.getName().endsWith(EXTENSION)) {
                continue;
            }
            String languageName = entry.getName().substring(LANG_DIRNAME.length(),
                    entry.getName().length() - EXTENSION.length());
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(new InputStreamReader(jar.getInputStream(entry)));
            languages.put(languageName, configuration);
        }

        if (!languages.containsKey(toLocaleString(defaultLanguage))) {
            defaultLanguage = null;
        }
    }

    public static String toLocaleString(Locale locale) {
        return (locale.getLanguage() + "_" + locale.getCountry()).toLowerCase();
    }
}
