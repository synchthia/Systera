package net.synchthia.systera.server;

import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.SysteraPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.Iterator;

public class ServerListener implements Listener {
    private final SysteraPlugin plugin;

    public ServerListener(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerPing(ServerListPingEvent event) {
        Iterator<Player> iterator = event.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            SysteraPlayer sp = plugin.getPlayerStore().get(player.getUniqueId());
            if (sp == null) {
                continue;
            }
            
            if (sp.getSettings().getVanish().getValue()) {
                iterator.remove();
            }
        }
    }
}
