package de.lightfall.core.web.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@NoArgsConstructor
public class PermissionMapEntity {
    private Map<String, Boolean> permissionMap;

    public PermissionMapEntity(Map<String, Boolean> permissionMap) {
        this.permissionMap = permissionMap;
    }
}
