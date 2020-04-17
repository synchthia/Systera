package net.synchthia.systera.group;

import lombok.Data;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.util.StringUtil;

import java.util.List;

@Data
public class Group {
    private String name;
    private String prefix;
    private List<String> globalPerms;
    private List<String> serverPerms;

    public Group(String name, String prefix, List<String> globalPerms, List<String> serverPerms) {
        this.name = name;
        this.prefix = prefix;
        this.globalPerms = globalPerms;
        this.serverPerms = serverPerms;
    }

    public static Group fromProto(SysteraProtos.GroupEntry entry) {
        return new Group(entry.getGroupName(), entry.getGroupPrefix(), entry.getGlobalPermsList(), entry.getServerPermsList());
    }

    public String getPrefix() {
        return StringUtil.coloring(prefix);
    }
}