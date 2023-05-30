package net.synchthia.systera.player;

import lombok.NonNull;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerStore {
    private final SysteraPlugin plugin;
    private final Map<UUID, SysteraPlayer> players = new HashMap<>();

    public PlayerStore(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    public SysteraPlayer get(UUID uuid) {
        return this.players.get(uuid);
    }

    public void add(UUID uuid, SysteraPlayer sp) {
        this.players.put(uuid, sp);
    }

    public void remove(UUID uuid) {
        this.players.remove(uuid);
    }

    public void clear() {
        this.players.clear();
    }

    public int size() {
        return this.players.size();
    }

    public Collection<SysteraPlayer> list() {
        return this.players.values();
    }

    public CompletableFuture<SysteraProtos.GetPlayerIdentityByNameResponse> fetchPlayerIdentity(@NonNull String name) {
        return plugin.getApiClient().getPlayerIdentity(name);
//        try {
//            SysteraProtos.GetPlayerIdentityByNameResponse result = this.plugin.getApiClient().getPlayerIdentity(name).get(5L, TimeUnit.SECONDS);
//            return result.getIdentity();
//        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
//            plugin.getLogger().log(Level.WARNING, "Failed fetch player identity: ", ex);
//            return null;
//        }
    }
}
