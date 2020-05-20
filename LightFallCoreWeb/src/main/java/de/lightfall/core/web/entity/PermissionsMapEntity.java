package de.lightfall.core.web.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@NoArgsConstructor
public class PermissionsMapEntity {
    private Map<String, Boolean> permissionMap;

    public PermissionsMapEntity(Map<String, Boolean> permissionMap) {
        this.permissionMap = permissionMap;
    }
}
