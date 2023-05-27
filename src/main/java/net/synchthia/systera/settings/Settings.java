package net.synchthia.systera.settings;

import lombok.Data;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Data
public class Settings {
    private final Player player;

    private JapanizeSettings japanize;
    private JoinMessageSettings joinMessage;
    private VanishSettings vanish;
    private GlobalChatSettings globalChat;

    public Settings(Player player, SysteraProtos.PlayerSettings settings) {
        this.player = player;

        this.japanize = new JapanizeSettings(settings.getJapanize());
        this.joinMessage = new JoinMessageSettings(settings.getJoinMessage());
        this.vanish = new VanishSettings(settings.getVanish());
        this.globalChat = new GlobalChatSettings(settings.getGlobalChat());
    }

    public Map<String, BaseSettings> getSettings() {
        Map<String, BaseSettings> settings = new HashMap<>();
        settings.put("japanize", this.japanize);
        settings.put("join_message", this.joinMessage);
        settings.put("vanish", this.vanish);

        if (SysteraPlugin.isEnableGlobalChat()) {
            settings.put("global_chat", this.globalChat);
        }

        return settings;
    }

    public SysteraProtos.PlayerSettings toProto() {
        return SysteraProtos.PlayerSettings.newBuilder()
                .setJapanize(this.japanize.getValue())
                .setJoinMessage(this.joinMessage.getValue())
                .setVanish(this.vanish.getValue())
                .setGlobalChat(this.globalChat.getValue())
                .build();
    }
}
