package net.synchthia.systera.i18n;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import lombok.Cleanup;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.ChatColor;
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
    private static final String DEFAULT_MESSAGES_FILENAME = "en_us" + EXTENSION;
    private static final String LANG_DIRNAME = "lang/";

    private final JavaPlugin plugin;
    private final Map<String, Configuration> languages = new HashMap<>();
    @NonNull
    private Configuration defaultMessages;
    private String defaultLanguage = "en_us";

    public I18nManager(@NonNull JavaPlugin plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;

        loadDefaultMessages();
        loadLanguageFiles();
    }

    private String[] format(String[] base, Object... extra) {
        for (int i = 0; i < base.length; i++) {
            base[i] = ChatColor.translateAlternateColorCodes('&', base[i]);
            base[i] = MessageFormat.format(base[i], extra);
        }
        return base;
    }

    @SuppressWarnings("unchecked")
    public String[] get(String language, @NonNull String key, Object... extra) {
        Object object = getLanguage(language).get(key);
        if (object == null) {
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

    public Configuration getLanguage(String languageName) {
        Configuration configuration = null;
        if (languageName != null) {
            configuration = languages.get(languageName);
        }
        if (configuration == null && defaultLanguage != null) {
            configuration = languages.get(defaultLanguage);
        }
        if (configuration == null) {
            configuration = defaultMessages;
        }
        return configuration;
    }

    public List<String> getList(String language, @NonNull String key, Object... extra) {
        String[] array = get(language, key, extra);
        return array == null ? null : Arrays.asList(array);
    }

    public String getString(String language, @NonNull String key, Object... extra) {
        String[] array = get(language, key, extra);
        return array == null ? null : Joiner.on('\n').join(array);
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

        if (!languages.containsKey(defaultLanguage)) {
            defaultLanguage = null;
        }
    }
}
