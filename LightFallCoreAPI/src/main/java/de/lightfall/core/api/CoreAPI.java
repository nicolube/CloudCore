package de.lightfall.core.api;

import com.j256.ormlite.support.ConnectionSource;
import de.lightfall.core.api.usermanager.UserManager;

public interface CoreAPI {

    /**
     * Gets the current {@link UserManager} singleton
     *
     * @return you the current instance of the user manager.
     */
    public UserManager getUserManager();

    /**
     * Gets the current {@link ConnectionSource} singleton
     *
     * @return you the current databaseConnection.
     */
    public ConnectionSource getConnectionSource();

    /**
     * Gets the current {@link CoreAPI} singleton
     *
     * @return you the current instance of the CoreAPI.
     */
    public static CoreAPI getInstance() {
        return Util.coreInstance;
    }
}
