package net.synchthia.systera.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.SysteraPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class ChatListener implements Listener {
    private final SysteraPlugin plugin;
    private final Japanize japanize;

    public ChatListener(SysteraPlugin plugin) {
        this.plugin = plugin;
        this.japanize = new Japanize();
    }

//    @EventHandler
//    public void onDebugChat(AsyncChatEvent event) {
//        Player player = event.getPlayer();
//
//        plugin.getServer().getConsoleSender().sendMessage(event.message());
//
////        event.message(Component.empty()
////                .append(player.name())
////                .append(Component.text(": "))
////                .append(event.originalMessage()));
//
//        event.renderer((source, sourceDisplayName, message, viewer) -> event.message());
//    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        SysteraPlayer systeraPlayer = plugin.getPlayerStore().get(player.getUniqueId());
        boolean isGlobal = SysteraPlugin.isEnableGlobalChat() && systeraPlayer.getSettings().getGlobalChat().getValue();

        event.message(Component.empty());

        Component format = Component.empty();

        // Server Name
        if (SysteraPlugin.isEnableGlobalChat()) {
            if (isGlobal) {
                format = format.append(Component
                        .text(String.format("[%s]", SysteraPlugin.getServerId()))
                        .color(NamedTextColor.GRAY)
                );
            } else {
                format = format.append(Component
                        .text("[local]")
                        .color(NamedTextColor.DARK_GRAY)
                );
            }
        }

        // Player Name
        format = format.append(player.displayName())
                .color(NamedTextColor.GRAY);

        // Colon
        format = format.append(Component.text(": "))
                .color(NamedTextColor.GREEN)
                .append(Component.empty())
                .color(NamedTextColor.WHITE);

        String japanizeMsg = "";
        if (systeraPlayer.getSettings().getJapanize().getValue()) {
            String converted = japanize.convert(event.signedMessage().message());
            if (!converted.isEmpty()) {
                japanizeMsg = converted;
            }
        }

        // イベントプレイヤーが含まれているPIを受信者から消す
        for (Player receivePlayer : plugin.getServer().getOnlinePlayers()) {
            for (SysteraProtos.PlayerIdentity receivePI : plugin.getPlayerStore().get(receivePlayer.getUniqueId()).getIgnoreList()) {
                if (player.getUniqueId().equals(APIClient.toUUID(receivePI.getUuid()))) {
                    event.viewers().remove(receivePlayer);
                }
            }
        }

        String globalFormat;
        Component localFormat;
        if (japanizeMsg.equals("")) {
            localFormat = format.append(event.originalMessage());
            globalFormat = MiniMessage.miniMessage().serialize(event.originalMessage());
        } else {
            localFormat = format
                    .append(Component.text(japanizeMsg))
                    .append(Component.text(" (" + event.signedMessage().message() + ")").color(NamedTextColor.GRAY));
            globalFormat = MiniMessage.miniMessage().serialize(Component
                    .text(japanizeMsg)
                    .append(Component.text(" (" + event.signedMessage().message() + ")").color(NamedTextColor.GRAY)));
        }

        event.renderer((source, sourceDisplayName, message, viewer) -> localFormat);

        if (isGlobal) {
            plugin.getApiClient().chat(APIClient.buildPlayerIdentity(player.getUniqueId(), MiniMessage.miniMessage().serialize(player.displayName())), SysteraPlugin.getServerId(), globalFormat).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    plugin.getLogger().log(Level.WARNING, "Failed send global chat event", throwable);
                }
            });
        }
    }
}