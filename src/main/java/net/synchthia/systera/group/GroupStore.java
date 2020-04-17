package net.synchthia.systera.group;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class GroupStore {
    private final SysteraPlugin plugin;
    private Map<String, Group> groups = new HashMap<>();

    public GroupStore(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    public Group get(String name) {
        return this.groups.get(name);
    }

    public void add(String name, Group group) {
        this.groups.put(name, group);
    }

    public void remove(String name) {
        this.groups.remove(name);
    }

    public int size() {
        return this.groups.size();
    }

    public CompletableFuture<SysteraProtos.FetchGroupsResponse> fetch() {
        return plugin.getApiClient().fetchGroups(SysteraPlugin.getServerId()).whenComplete((res, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed fetch group: ", throwable);
                return;
            }

            res.getGroupsList().forEach(e -> this.add(e.getGroupName(), Group.fromProto(e)));
        });
    }
}
