package net.synchthia.systera.player;

import lombok.Data;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.group.Group;
import net.synchthia.systera.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Data
public class SysteraPlayer {
    private final SysteraPlugin plugin;
    private final Player player;
    private UUID uuid;
    private String name;
    private List<String> groups;
    private Settings settings;

    // Group
    private PermissionAttachment attachment;

    public SysteraPlayer(SysteraPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.attachment = player.addAttachment(this.plugin);
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

    // TODO: to static method?
    private void fromProto(SysteraProtos.PlayerEntry entry) {
        this.uuid = APIClient.toUUID(entry.getUuid());
        this.name = entry.getName();
        this.groups = entry.getGroupsList();
        this.settings = new Settings(this.player, entry.getSettings());
    }

    public String getPrefix() {
        Group group;
        if (groups.size() >= 2) {
            group = plugin.getGroupStore().get(groups.get(1));
        } else {
            group = plugin.getGroupStore().get(groups.get(0));
        }

        if (group != null) {
            return group.getPrefix();
        } else {
            return "";
        }
    }

    public void applyPermissionsByGroup() {
        refreshAttachment();

        player.setPlayerListName(getPrefix() + player.getName());

        this.groups.forEach(groupName -> {
            Group group = plugin.getGroupStore().get(groupName);
            if (group != null) {
                applyPermissions(group.getGlobalPerms());
                applyPermissions(group.getServerPerms());
            }
        });
    }

    private void refreshAttachment() {
        if (this.attachment != null) {
            player.removeAttachment(this.attachment);
            this.attachment = player.addAttachment(this.plugin);
        }
    }

    public void applyPermissions(List<String> permissions) {
        permissions.forEach(perm -> {
            if (perm.startsWith("-")) {
                attachment.setPermission(perm.replaceFirst("-", ""), false);
            } else {
                attachment.setPermission(perm, true);
            }
        });

        player.recalculatePermissions();

        if (player.hasPermission("systera.op")) {
            player.setOp(true);
        } else {
            player.setOp(false);
        }
    }
}
