package net.synchthia.systera.player;

import net.synchthia.systera.SysteraPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
}
