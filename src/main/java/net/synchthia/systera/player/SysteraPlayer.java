package net.synchthia.systera.player;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.group.Group;
import net.synchthia.systera.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SysteraPlayer {
    private final SysteraPlugin plugin;

    @Getter
    private final Player player;

    @Getter
    private UUID uuid;

    @Getter
    private String name;

    @Getter
    private List<String> groups;

    @Getter
    private Settings settings;

    private List<SysteraProtos.PlayerIdentity> ignoreList;

    // Group
    @Getter
    private PermissionAttachment attachment;

    public SysteraPlayer(SysteraPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.attachment = player.addAttachment(this.plugin);
    }

    public List<SysteraProtos.PlayerIdentity> getIgnoreList() {
        return ignoreList;
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

    public CompletableFuture<SysteraProtos.ChatIgnoreResponse> ignorePlayer(SysteraProtos.PlayerIdentity targetPlayer) {
        return plugin.getApiClient().addChatIgnore(this.player.getUniqueId(), targetPlayer).whenComplete((res, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed ignore player: ", throwable);
            }
        });
    }

    public CompletableFuture<SysteraProtos.ChatIgnoreResponse> unIgnorePlayer(SysteraProtos.PlayerIdentity targetPlayer) {
        return plugin.getApiClient().removeChatIgnore(this.player.getUniqueId(), targetPlayer).whenComplete((res, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed unignore player: ", throwable);
            }
        });
    }

    public CompletableFuture<SysteraProtos.Empty> syncSettings() {
        SysteraProtos.PlayerSettings proto = this.getSettings().toProto();
        return plugin.getApiClient().setPlayerSettings(player.getUniqueId(), proto).whenComplete(((res, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed sync player settings: ", throwable);
            }
        }));
    }

    // TODO: to static method?
    private void fromProto(SysteraProtos.PlayerEntry entry) {
        this.uuid = APIClient.toUUID(entry.getUuid());
        this.name = entry.getName();
        this.groups = entry.getGroupsList();
        this.settings = new Settings(this.player, entry.getSettings());
        this.ignoreList = new ArrayList<>(entry.getPlayerIgnoreList());
    }

    public Component getPrefix() {
        Group group;
        if (groups.size() >= 2) {
            group = plugin.getGroupStore().get(groups.get(1));
        } else {
            group = plugin.getGroupStore().get(groups.get(0));
        }

        if (group != null) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(group.getPrefix());
        } else {
            return Component.empty();
        }
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
        applyPermissionsByGroup();
    }

    public void applyPermissionsByGroup() {
        refreshAttachment();

        player.playerListName(Component.empty()
                .append(getPrefix())
                .append(Component.text(player.getName()))
        );

        plugin.getLogger().log(Level.INFO, String.format("[%s] Group: %s", player.getName(), this.groups));

        this.groups.stream().map(groupName -> plugin.getGroupStore().get(groupName)).forEach(group -> {
            if (group != null) {
                applyPermissions(Arrays.stream(group.getPermissions().get("global")).toList());
                String[] serverPerms = group.getPermissions().get(SysteraPlugin.getServerId());
                if (serverPerms != null) {
                    applyPermissions(Arrays.stream(serverPerms).toList());
                }
            } else {
                plugin.getLogger().log(Level.WARNING, "Group is null");
            }
        });
    }

    public void refreshAttachment() {
        if (this.attachment != null) {
            player.removeAttachment(this.attachment);
        }

        this.attachment = player.addAttachment(this.plugin);
    }

    public void applyPermissions(List<String> permissions) {
        permissions.forEach((perm) -> {
            if (perm.startsWith("-")) {
                this.attachment.setPermission(perm.replaceFirst("-", ""), false);
            } else {
                this.attachment.setPermission(perm, true);
            }
        });

        player.recalculatePermissions();
        player.updateCommands();

        player.setOp(player.hasPermission("systera.op"));
    }
}
