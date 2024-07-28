package net.synchthia.systera.tablist;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TabList {
    private final SysteraPlugin plugin;
    private final String tabListHeader;
    private final String tabListFooter;

    public void sendHeaderFooter(Player player) {
        Component header = MiniMessage.miniMessage().deserialize(this.tabListHeader,
                Placeholder.unparsed("_server_id_", SysteraPlugin.getServerId()),
                Placeholder.unparsed("_server_name_", SysteraPlugin.getServerName()));

        Component footer = MiniMessage.miniMessage().deserialize(this.tabListFooter,
                Placeholder.unparsed("_server_id_", SysteraPlugin.getServerId()),
                Placeholder.unparsed("_server_name_", SysteraPlugin.getServerName()));

        player.sendPlayerListHeaderAndFooter(header, footer);
    }
}
