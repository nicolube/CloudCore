package de.cloud.core.common;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.cloud.core.common.models.*;
import de.cloud.core.api.config.DatabaseConfig;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class DatabaseProvider {
    private final JdbcConnectionSource connectionSource;
    private final Dao<UserInfoModel, Long> userInfoDao;
    private final Dao<UserModeInfoModel, Long> userModeInfoDao;
    private final Dao<PunishmentModel, Long> punishmentDao;
    private final Dao<MessageModel, Long> messageDao;
    private final Dao<StatModel, Long> statsDao;
    private final Dao<StatUpdateModel,Long> statsUpdateDao;
    private final Dao<ModeInstanceModel, Long> modeInstanceDao;
    private final Dao<ModeInstanceUserModel,Long> modeInstanceUserDao;
    private final Dao<ModeInstanceDataModel,Long> modeInstanceDataDao;

    private Dao<WebApiTokenModel, Long> webApiTokenDao;
    private Dao<TeamRecordModel, Long> webTeamRecordDao;
    private Dao<TeamAbsenceModel, Long> webTeamAbsenceDao;
    private Dao<StrikeTemplateModel, Long> webStrikeTemplateDao;
    private Dao<StrikeModel, Long> webStrikeDao;
    private Dao<InterComTokenModel,Long> interComTokenDao;

    @SneakyThrows
    public DatabaseProvider(DatabaseConfig config) {
        this.connectionSource = new JdbcPooledConnectionSource(config.getUrl(),
                config.getUser(), config.getPassword());
        
        this.messageDao = DaoManager.createDao(connectionSource, MessageModel.class);
        this.userInfoDao = DaoManager.createDao(this.connectionSource, UserInfoModel.class);
        this.userModeInfoDao = DaoManager.createDao(this.connectionSource, UserModeInfoModel.class);
        this.punishmentDao = DaoManager.createDao(this.connectionSource, PunishmentModel.class);
        this.statsDao = DaoManager.createDao(this.connectionSource, StatModel.class);
        this.statsUpdateDao = DaoManager.createDao(this.connectionSource, StatUpdateModel.class);
        this.modeInstanceDao = DaoManager.createDao(this.connectionSource, ModeInstanceModel.class);
        this.modeInstanceUserDao = DaoManager.createDao(this.connectionSource, ModeInstanceUserModel.class);
        this.modeInstanceDataDao = DaoManager.createDao(this.connectionSource, ModeInstanceDataModel.class);
        TableUtils.createTableIfNotExists(connectionSource, MessageModel.class);
        TableUtils.createTableIfNotExists(connectionSource, UserInfoModel.class);
        TableUtils.createTableIfNotExists(connectionSource, UserModeInfoModel.class);
        TableUtils.createTableIfNotExists(connectionSource, PunishmentModel.class);
        TableUtils.createTableIfNotExists(connectionSource, StatModel.class);
        TableUtils.createTableIfNotExists(connectionSource, StatUpdateModel.class);
        TableUtils.createTableIfNotExists(connectionSource, ModeInstanceModel.class);
        TableUtils.createTableIfNotExists(connectionSource, ModeInstanceUserModel.class);
        TableUtils.createTableIfNotExists(connectionSource, ModeInstanceDataModel.class);
    }

    @SneakyThrows
    public void setupWebApi() {
        this.webApiTokenDao = DaoManager.createDao(this.connectionSource, WebApiTokenModel.class);;
        this.interComTokenDao = DaoManager.createDao(this.connectionSource, InterComTokenModel.class);
        this.webTeamRecordDao = DaoManager.createDao(this.connectionSource, TeamRecordModel.class);
        this.webTeamAbsenceDao = DaoManager.createDao(this.connectionSource, TeamAbsenceModel.class);
        this.webStrikeTemplateDao = DaoManager.createDao(this.connectionSource, StrikeTemplateModel.class);
        this.webStrikeDao = DaoManager.createDao(this.connectionSource, StrikeModel.class);
        TableUtils.createTableIfNotExists(connectionSource, WebApiTokenModel.class);
        TableUtils.createTableIfNotExists(connectionSource, InterComTokenModel.class);
        TableUtils.createTableIfNotExists(connectionSource, TeamRecordModel.class);
        TableUtils.createTableIfNotExists(connectionSource, TeamAbsenceModel.class);
        TableUtils.createTableIfNotExists(connectionSource, StrikeModel.class);
        TableUtils.createTableIfNotExists(connectionSource, StrikeTemplateModel.class);
        TableUtils.createTableIfNotExists(connectionSource, StrikeModel.class);
    }

    @SneakyThrows
    public void disconnect() {
        this.connectionSource.close();
    }
}