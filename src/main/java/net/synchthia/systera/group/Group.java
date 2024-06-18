package net.synchthia.systera.group;

import lombok.Data;
import net.synchthia.api.systera.SysteraProtos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Group {
    private String name;
    private String prefix;
    private Map<String, String[]> permissions = new HashMap<>();

    public Group(String name, String prefix, List<SysteraProtos.PermissionsEntry> permissionsList) {
        this.name = name;
        this.prefix = prefix;

        for (SysteraProtos.PermissionsEntry permissionsEntry : permissionsList) {
            this.permissions.put(
                    permissionsEntry.getServerName(),
                    permissionsEntry.getPermissionsList().stream().toList().toArray(new String[0])
            );
        }
    }

    public static Group fromProto(SysteraProtos.GroupEntry entry) {
        return new Group(entry.getGroupName(), entry.getGroupPrefix(), entry.getPermissionsList());
    }
}