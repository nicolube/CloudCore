package de.lightfall.core;

import co.aikar.commands.CommandManager;
import com.j256.ormlite.dao.Dao;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.common.models.PunishmentModel;
import de.lightfall.core.common.models.UserInfoModel;
import de.lightfall.core.common.models.UserModeInfoModel;

public interface InternalCoreAPI extends CoreAPI {

    public Dao<UserInfoModel, Long> getUserInfoDao();

    public Dao<UserModeInfoModel, Long> getUserModeInfoDao();

    public Dao<PunishmentModel, Long> getPunishmentDao();

    public CommandManager getCommandManager();
}
