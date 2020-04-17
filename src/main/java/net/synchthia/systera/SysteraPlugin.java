package net.synchthia.systera;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandIssuer;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import net.synchthia.systera.commands.APICommand;
import net.synchthia.systera.commands.SettingsCommand;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.i18n.I18nManager;
import net.synchthia.systera.player.PlayerListener;
import net.synchthia.systera.player.PlayerStore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SysteraPlugin extends JavaPlugin {
    // Environment Variables
    @Getter
    private final static String serverId = System.getenv("SERVER_ID") != null ? System.getenv("SERVER_ID") : "unknown";

    @Getter
    private final static String serverName = System.getenv("SERVER_NAME") != null ? System.getenv("SERVER_NAME") : "Unknown";

    @Getter
    private final static String apiAddress = System.getenv("SYSTERA_API_ADDRESS") != null ? System.getenv("SYSTERA_API_ADDRESS") : "localhost:17300";

    @Getter
    private static SysteraPlugin instance;

    @Getter
    @Setter
    private boolean started;

    // API
    @Getter
    private APIClient apiClient;

    // Store
    @Getter
    private PlayerStore playerStore;

    // Commands
    private BukkitCommandManager cmdManager;

    @Override
    public void onEnable() {
        try {
            instance = this;

            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            I18n.setI18nManager(new I18nManager(this));

            this.playerStore = new PlayerStore(this);

            registerAPI();
            registerEvents();
            registerCommands();

            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

            this.getLogger().log(Level.INFO, "Enabled: " + this.getName());
            this.started = true;
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Exception threw while onEnable: ", e);
        }
    }

    private void registerAPI() {
        this.getLogger().log(Level.INFO, "API Address: " + apiAddress);

        this.apiClient = new APIClient(apiAddress);

        // TODO: FetchPlayerProfile (or Init?)
    }

    private void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
    }

    private void registerCommands() {
        this.cmdManager = new BukkitCommandManager(this);

        cmdManager.getCommandCompletions().registerCompletion("punish_reason", c -> ImmutableList.of(
                "Chat Spam (チャットスパム)",
                "Advertise (広告)",
                "Glitch (バグや不具合の意図的な不正利用)",
                "Obscenity / NSFW Content (不適切なコンテンツ) ",
                "Griefing (他のユーザーへの迷惑行為)",
                "Violent Language (不適切な発言)",
                "Watch your language (不適切な発言)",
                "Hacking - Fly",
                "Hacking - Nuke",
                "Hacking - FastRun",
                "Hacking - FastEat"
        ));

        cmdManager.getCommandCompletions().registerCompletion("player_settings", c -> {
            if (c.getSender() instanceof Player) {
                Player player = c.getPlayer();
                return ImmutableList.copyOf(playerStore.get(player.getUniqueId()).getSettings().getSettings().keySet());
            }
            return ImmutableList.of();
        });

        this.cmdManager.registerCommand(new APICommand(this));
        this.cmdManager.registerCommand(new SettingsCommand(this));
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "Disabled: " + this.getName());
        this.started = false;
    }
}
