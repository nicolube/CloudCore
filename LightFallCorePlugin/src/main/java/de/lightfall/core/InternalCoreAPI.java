package de.lightfall.core;

import co.aikar.commands.CommandManager;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.common.DatabaseProvider;

public interface InternalCoreAPI extends CoreAPI {

    DatabaseProvider getDatabaseProvider();

    CommandManager getCommandManager();
}
