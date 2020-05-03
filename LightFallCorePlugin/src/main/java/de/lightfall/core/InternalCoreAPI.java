package de.lightfall.core;

import com.j256.ormlite.dao.Dao;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.models.UserModeInfoModel;
import lombok.Getter;

public interface InternalCoreAPI extends CoreAPI {

    public Dao<UserInfoModel, Long> getPlayerDao();

    public Dao<UserModeInfoModel, Long> getPlayerModeDao();
}
