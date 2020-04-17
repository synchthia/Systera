package net.synchthia.systera.settings;

import lombok.Data;
import net.synchthia.api.systera.SysteraProtos;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Data
public class Settings {
    private final Player player;

    private JapanizeSettings japanize;
    private JoinMessageSettings joinMessage;
    private VanishSettings vanish;

    public Settings(Player player, SysteraProtos.PlayerSettings settings) {
        this.player = player;

        this.japanize = new JapanizeSettings(settings.getJapanize());
        this.joinMessage = new JoinMessageSettings(settings.getJoinMessage());
        this.vanish = new VanishSettings(settings.getVanish());
    }

    public Map<String, BaseSettings> getSettings() {
        Map<String, BaseSettings> settings = new HashMap<>();
        settings.put("japanize", this.japanize);
        settings.put("join_message", this.joinMessage);
        if (this.player.hasPermission("systera.vanish")) {
            settings.put("vanish", this.vanish);
        }

        return settings;
    }

    public SysteraProtos.PlayerSettings toProto() {
        return SysteraProtos.PlayerSettings.newBuilder()
                .setJapanize(this.japanize.getValue())
                .setJoinMessage(this.joinMessage.getValue())
                .setVanish(this.vanish.getValue())
                .build();
    }
}
