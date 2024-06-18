package net.synchthia.systera;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.synchthia.systera.chat.ChatListener;
import net.synchthia.systera.commands.*;
import net.synchthia.systera.group.GroupStore;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.i18n.I18nManager;
import net.synchthia.systera.player.PlayerListener;
import net.synchthia.systera.player.PlayerStore;
import net.synchthia.systera.player.SysteraPlayer;
import net.synchthia.systera.punishments.PunishAPI;
import net.synchthia.systera.server.ServerListener;
import net.synchthia.systera.stream.RedisClient;
import net.synchthia.systera.tablist.TabListModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.concurrent.TimeUnit;
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
    private final static boolean enableGlobalChat = System.getenv("SYSTERA_GLOBAL_CHAT") != null && Boolean.parseBoolean(System.getenv("SYSTERA_GLOBAL_CHAT"));

    @Getter
    private static SysteraPlugin instance;

    @Getter
    @Setter
    private boolean started;

    @Getter
    private ProtocolManager protocolManager;

    // Stream
    @Getter
    private RedisClient redisClient;

    // API
    @Getter
    private APIClient apiClient;
    @Getter
    private PunishAPI punishAPI;

    // Store
    @Getter
    private PlayerStore playerStore;
    @Getter
    private GroupStore groupStore;

    // Commands
    private AnnotationParser<CommandSender> annotationParser;
    private LegacyPaperCommandManager<CommandSender> commandManager;

    // TabList
    @Getter
    private TabListModule tabListModule;

    @Override
    public void onEnable() {
        try {
            instance = this;

            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            I18n.setI18nManager(new I18nManager(this));

            registerRedis();

            registerAPI();
            registerEvents();
            registerCommands();

            this.protocolManager = ProtocolLibrary.getProtocolManager();

            this.tabListModule = new TabListModule(this);
            tabListModule.setup();

            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener(this));

            this.getLogger().log(Level.INFO, "Enabled: " + this.getName());
            this.started = true;
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Exception threw while onEnable: ", e);
            this.getServer().shutdown();
        }
    }

    public void registerRedis() throws InterruptedException {
        String hostname = "localhost";
        Integer port = 6379;

        String redisAddress = System.getenv("SYSTERA_REDIS_ADDRESS");
        if (redisAddress != null) {
            if (redisAddress.contains(":")) {
                String[] splited = redisAddress.split(":");
                hostname = splited[0];
                port = Integer.valueOf(splited[1]);
            } else {
                hostname = redisAddress;
            }
        }

        getLogger().log(Level.INFO, "Redis Address: " + hostname + ":" + port);
        redisClient = new RedisClient(SysteraPlugin.getServerId(), hostname, port);
    }

    @SneakyThrows
    public void registerAPI() {
        this.getLogger().log(Level.INFO, "API Address: " + apiAddress);
        this.apiClient = new APIClient(apiAddress);

        // API Client
        this.punishAPI = new PunishAPI(this);

        // Initialize store
        this.playerStore = new PlayerStore(this);
        this.groupStore = new GroupStore(this);

        // Sync GroupStore
        getGroupStore().clear();
        getGroupStore().fetch().get(5, TimeUnit.SECONDS);

        // Sync PlayerStore
        getPlayerStore().clear();
        for (Player player : this.getServer().getOnlinePlayers()) {
            SysteraPlayer pd = new SysteraPlayer(this, player);
            pd.fetch().get(5, TimeUnit.SECONDS);

            getPlayerStore().add(player.getUniqueId(), pd);
            pd.applyPermissionsByGroup();
        }
    }

    private void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new ServerListener(this), this);
    }

    private void registerCommands() {
        this.commandManager = LegacyPaperCommandManager.createNative(this, ExecutionCoordinator.simpleCoordinator());

        if (this.commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.commandManager.registerAsynchronousCompletions();
        }

        this.commandManager.exceptionController().registerHandler(InvalidSyntaxException.class, e ->
                e.context().sender().sendRichMessage(String.format("<yellow>Usage:</yellow> <green>/%s</green>", e.exception().correctSyntax()))
        );

        this.commandManager.exceptionController().registerHandler(NoPermissionException.class, e ->
                I18n.sendMessage(e.context().sender(), "general.error.permission_denied")
        );

        this.annotationParser = new AnnotationParser<>(this.commandManager, CommandSender.class);
        this.annotationParser.parse(
                new CommandSuggestions(this),
                new AnnounceCommand(this),
                new APICommand(this),
                new DispatchCommand(this),
                new IgnoreCommand(this),
                new ListCommand(this),
                new PunishCommand(this),
                new ReportCommand(this),
                new RunasCommand(this),
                new SeenCommand(this),
                new SettingsCommand(this),
                new SysteraCommand(this),
                new TellCommand(this),
                new UnBanCommand(this)
        );
    }

    @Override
    @SneakyThrows
    public void onDisable() {
        apiClient.shutdown();
        redisClient.disconnect();

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);

        tabListModule.release();

        this.getLogger().log(Level.INFO, "Disabled: " + this.getName());
        this.started = false;
    }
}
