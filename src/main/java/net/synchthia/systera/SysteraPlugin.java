package net.synchthia.systera;

import co.aikar.commands.BukkitCommandManager;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.synchthia.api.systera.SysteraProtos;
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
    private BukkitCommandManager cmdManager;

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
        this.cmdManager = new BukkitCommandManager(this);

        cmdManager.getCommandCompletions().registerCompletion("all_and_players", c -> {
            List<String> players = new java.util.ArrayList<>(getServer().getOnlinePlayers().stream().map(Player::getName).toList());
            players.add("*");
            return ImmutableList.copyOf(players);
        });

        cmdManager.getCommandCompletions().registerCompletion("punish_reason", c -> ImmutableList.of(
                "Chat Spam (チャットスパム)",
                "Glitch (バグや不具合の意図的な不正利用)",
                "NSFW Content (不適切なコンテンツ) ",
                "Griefing (他のユーザーへの迷惑行為)",
                "Violent Language (不適切な発言)",
                "Hack / Cheat (チート行為)",
                "Others / その他 -> ..."
        ));

        cmdManager.getCommandCompletions().registerCompletion("player_settings", c -> {
            if (c.getSender() instanceof Player) {
                Player player = c.getPlayer();
                SysteraPlayer sp = playerStore.get(player.getUniqueId());
                List<String> set = sp.getSettings().getSettings().keySet().stream().filter((x) -> sp.getSettings().getSettings().get(x).hasPermission(player)).collect(Collectors.toList());
                return ImmutableList.copyOf(set);
            }
            return ImmutableList.of();
        });

        cmdManager.getCommandCompletions().registerCompletion("ignored_players", c -> {
            if (c.getSender() instanceof Player) {
                Player player = c.getPlayer();
                SysteraPlayer sp = playerStore.get(player.getUniqueId());
                return ImmutableList.copyOf(sp.getIgnoreList().stream().map(SysteraProtos.PlayerIdentity::getName).toList());
            }
            return ImmutableList.of();
        });

        this.cmdManager.registerCommand(new AnnounceCommand(this));
        this.cmdManager.registerCommand(new APICommand(this));
        this.cmdManager.registerCommand(new ListCommand(this));
        this.cmdManager.registerCommand(new ReportCommand(this));
        this.cmdManager.registerCommand(new SettingsCommand(this));
        this.cmdManager.registerCommand(new SysteraCommand(this));
        this.cmdManager.registerCommand(new PunishCommand(this));
        this.cmdManager.registerCommand(new UnBanCommand(this));
        this.cmdManager.registerCommand(new SeenCommand(this));
        this.cmdManager.registerCommand(new TellCommand(this));
        this.cmdManager.registerCommand(new IgnoreCommand(this));
        this.cmdManager.registerCommand(new RunasCommand(this));
        this.cmdManager.registerCommand(new DispatchCommand(this));
    }

    @Override
    @SneakyThrows
    public void onDisable() {
        apiClient.shutdown();
        redisClient.disconnect();

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);

        this.getLogger().log(Level.INFO, "Disabled: " + this.getName());
        this.started = false;
    }
}
