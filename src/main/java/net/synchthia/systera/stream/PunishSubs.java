package net.synchthia.systera.stream;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.punishments.PunishAPI;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;

public class PunishSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.PunishmentStream stream = APIClient.punishmentStreamFromJson(message);
        assert stream != null;
        switch (stream.getType()) {
            case PUNISH:
                PunishAPI punishAPI = plugin.getPunishAPI();
                SysteraProtos.PunishStreamEntry punishStreamEntry = stream.getPunishStreamEntry();
                SysteraProtos.PunishEntry punishEntry = punishStreamEntry.getEntry();

                if (punishStreamEntry.getRequireExecute()) {
                    plugin.getLogger().log(Level.INFO, "[Punish] Run execute remotely");
                    punishAPI.execution(APIClient.toUUID(punishEntry.getPunishedTo().getUuid()), punishEntry.getLevel(), punishEntry.getReason(), punishEntry.getExpire());
                }

                punishAPI.broadcast(punishEntry.getLevel(), punishEntry.getPunishedTo().getName(), punishEntry.getReason());
                break;

            case REPORT:
                SysteraProtos.ReportEntry reportEntry = stream.getReportEntry();
                plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().getOnlinePlayers().forEach(player -> {
                    if (player.hasPermission("systera.report.receive")) {
                        I18n.sendMessage(player, "report.notify", Placeholder.unparsed("_server_name_", reportEntry.getServer()), Placeholder.unparsed("_player_from_", reportEntry.getFrom().getName()), Placeholder.unparsed("_player_to_", reportEntry.getTo().getName()), Placeholder.unparsed("_message_", reportEntry.getMessage()));
                    }
                }));
                break;
        }
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        plugin.getLogger().log(Level.INFO, "P Subscribed : " + pattern);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        plugin.getLogger().log(Level.INFO, "P UN Subscribed : " + pattern);
    }
}
