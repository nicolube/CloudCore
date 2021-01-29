package de.cloud.core.api;

public interface ICorePlugin {

    /**
     * Will be executed when the API has been fully initialised.
     */
    void onApiEnable();

    /**
     * Gets name of plugin
     *
     * @return name of plugin
     */
    String getName();
}
