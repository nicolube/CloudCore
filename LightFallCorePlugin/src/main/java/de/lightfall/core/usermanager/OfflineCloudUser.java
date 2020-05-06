package de.lightfall.core.usermanager;

import java.util.UUID;

public class OfflineCloudUser extends CloudUser {

    public OfflineCloudUser(UUID uuid, String realName, long databaseId, UserManager userManager) {
        super(uuid, realName, databaseId, userManager);
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public <T> T getPlayer() {
        return null;
    }
}
