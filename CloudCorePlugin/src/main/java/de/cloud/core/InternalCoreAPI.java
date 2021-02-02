package de.cloud.core;

import co.aikar.commands.CommandManager;
import de.cloud.core.api.CoreAPI;
import de.cloud.core.common.DatabaseProvider;

public interface InternalCoreAPI extends CoreAPI {

    DatabaseProvider getDatabaseProvider();

    CommandManager getCommandManager();

    void configChatColor(CommandManager commandManager);

}
