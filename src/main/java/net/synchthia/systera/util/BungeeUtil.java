package net.synchthia.systera.util;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeUtil {
    public static void disconnect(SysteraPlugin plugin, Player player, String reason) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);
            dataOut.writeUTF("KickPlayer");
            dataOut.writeUTF(player.getName());
            dataOut.writeUTF(reason);
            player.sendPluginMessage(plugin, "BungeeCord", byteOut.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
