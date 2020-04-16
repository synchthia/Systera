package net.synchthia.systera.player;

import com.google.protobuf.ProtocolStringList;
import lombok.Data;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.settings.Settings;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Data
public class SysteraPlayer {
    private final SysteraPlugin plugin;
    private final Player player;
    private UUID uuid;
    private String name;
    private ProtocolStringList groups;
    private Settings settings;

    public SysteraPlayer(SysteraPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public CompletableFuture<SysteraProtos.InitPlayerProfileResponse> init(String address, String hostname) {
        // NOTE: Do not tap twice. This action will be update Last Login...
        return plugin.getApiClient().initPlayerProfile(this.player.getUniqueId(), player.getName(), address, hostname).whenComplete((res, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed initialize player profile: ", throwable);
                return;
            }

            fromProto(res.getEntry());
        });
    }

    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetch() {
        return plugin.getApiClient().fetchPlayerProfile(this.player.getUniqueId()).whenComplete((res, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed fetch player profile: ", throwable);
                return;
            }

            fromProto(res.getEntry());
        });
    }

    private void fromProto(SysteraProtos.PlayerEntry entry) {
        this.uuid = APIClient.toUUID(entry.getUuid());
        this.name = entry.getName();
        this.groups = entry.getGroupsList();
        this.settings = new Settings(this.player, entry.getSettings());
    }
}
