package de.lightfall.core.api;

import com.j256.ormlite.support.ConnectionSource;
import de.lightfall.core.api.message.IMessageProvider;
import de.lightfall.core.api.usermanager.IUserManager;

public interface CoreAPI {

    /**
     * Setts the Mode if you want to be able to access IPayerModeInfo
     *
     * @param mode
     */
    public void setMode(boolean mode);

    /**
     * Gets the current {@link IUserManager} singleton
     *
     * @return you the current instance of the user manager.
     */
    public IUserManager getUserManager();

    /**
     * Gets the current {@link ConnectionSource} singleton
     *
     * @return you the current databaseConnection.
     */
    public ConnectionSource getConnectionSource();


    /**
     * Gets the current {@link IMessageProvider} singleton
     *
     * @return you the current messagesProvider.
     */
    public IMessageProvider getMessageProvider();

    /**
     * Gets the current {@link CoreAPI} singleton
     *
     * @return you the current instance of the CoreAPI.
     */
    public static CoreAPI getInstance() {
        return Util.coreInstance;
    }
}
