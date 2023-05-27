package net.synchthia.systera.stream;

import lombok.SneakyThrows;
import net.synchthia.systera.SysteraPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class RedisClient {
    private final JedisPool pool;
    private final SysteraPlugin plugin = SysteraPlugin.getInstance();
    private final String name;
    private final String hostname;
    private final Integer port;

    private SystemSubs systemSubs;
    private PlayerSubs playerSubs;
    private PunishSubs punishSubs;
    private GroupSubs groupSubs;
    private ChatSubs chatSubs;

    public RedisClient(String name, String hostname, Integer port) {
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.pool = new JedisPool(hostname, port);

        runSystemTask();
        runPlayerTask();
        runPunishTask();
        runGroupTask();
        runChatTask();
    }

    private void runSystemTask() {
        String taskName = "[SYSTEM_TASK] ";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    systemSubs = new SystemSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    Jedis jedis = pool.getResource();

                    // Subscribe
                    jedis.psubscribe(systemSubs, "systera.system.global", "systera.system." + name);
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runSystemTask();
                }
            }
        });
    }

    private void runPlayerTask() {
        String taskName = "[PLAYER_TASK] ";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    playerSubs = new PlayerSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    Jedis jedis = pool.getResource();

                    // Subscribe
                    jedis.psubscribe(playerSubs, "systera.player.global", "systera.player." + name);
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runPlayerTask();
                }
            }
        });
    }

    private void runPunishTask() {
        String taskName = "[PUNISH_TASK] ";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    punishSubs = new PunishSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    Jedis jedis = pool.getResource();

                    // Subscribe
                    jedis.psubscribe(punishSubs, "systera.punishment.global");
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runPunishTask();
                }
            }
        });
    }

    private void runGroupTask() {
        String taskName = "[GROUP_TASK] ";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    groupSubs = new GroupSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    Jedis jedis = pool.getResource();

                    // Subscribe
                    jedis.psubscribe(groupSubs, "systera.group.global", "systera.group." + name);
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runGroupTask();
                }
            }
        });
    }

    private void runChatTask() {
        String taskName = "[CHAT_TASK] ";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    chatSubs = new ChatSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    Jedis jedis = pool.getResource();

                    // Subscribe
                    jedis.psubscribe(chatSubs, "systera.chat.global", "systera.chat." + name);
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runChatTask();
                }
            }
        });
    }

    public void disconnect() {
        if (systemSubs != null) {
            systemSubs.punsubscribe();
        }
        if (playerSubs != null) {
            playerSubs.punsubscribe();
        }
        if (punishSubs != null) {
            punishSubs.punsubscribe();
        }
        if (groupSubs != null) {
            groupSubs.punsubscribe();
        }
        if (chatSubs != null) {
            chatSubs.punsubscribe();
        }

        pool.close();
        pool.destroy();
    }
}
