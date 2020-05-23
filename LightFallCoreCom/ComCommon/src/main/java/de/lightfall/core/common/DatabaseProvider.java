package de.lightfall.core.common;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.lightfall.core.api.config.DatabaseConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import de.lightfall.core.common.models.*;

@Getter
public class DatabaseProvider {
    private final JdbcConnectionSource connectionSource;
    private final Dao<UserInfoModel, Long> userInfoDao;
    private final Dao<UserModeInfoModel, Long> userModeInfoDao;
    private final Dao<PunishmentModel, Long> punishmentDao;
    private final Dao<MessageModel, Long> messageDao;

    private Dao<WebApiTokenModel, Long> webApiTokenDao;
    private Dao<TeamRecordModel, Long> webTeamRecordDao;
    private Dao<TeamAbsenceModel, Long> webTeamAbsenceDao;
    private Dao<StrikeTemplateModel, Long> webStrikeTemplateDao;
    private Dao<StrikeModel, Long> webStrikeDao;

    @SneakyThrows
    public DatabaseProvider(DatabaseConfig config) {
        this.connectionSource = new JdbcPooledConnectionSource(config.getUrl(),
                config.getUser(), config.getPassword());
        this.messageDao = DaoManager.createDao(connectionSource, MessageModel.class);
        this.userInfoDao = DaoManager.createDao(this.connectionSource, UserInfoModel.class);
        this.userModeInfoDao = DaoManager.createDao(this.connectionSource, UserModeInfoModel.class);
        this.punishmentDao = DaoManager.createDao(this.connectionSource, PunishmentModel.class);
        TableUtils.createTableIfNotExists(connectionSource, MessageModel.class);
        TableUtils.createTableIfNotExists(connectionSource, UserInfoModel.class);
        TableUtils.createTableIfNotExists(connectionSource, UserModeInfoModel.class);
        TableUtils.createTableIfNotExists(connectionSource, PunishmentModel.class);
    }

    @SneakyThrows
    public void setupWebApi() {
        this.webApiTokenDao = DaoManager.createDao(this.connectionSource, WebApiTokenModel.class);
        this.webTeamRecordDao = DaoManager.createDao(this.connectionSource, TeamRecordModel.class);
        this.webTeamAbsenceDao = DaoManager.createDao(this.connectionSource, TeamAbsenceModel.class);
        this.webStrikeTemplateDao = DaoManager.createDao(this.connectionSource, StrikeTemplateModel.class);
        this.webStrikeDao = DaoManager.createDao(this.connectionSource, StrikeModel.class);
        TableUtils.createTableIfNotExists(connectionSource, WebApiTokenModel.class);
        TableUtils.createTableIfNotExists(connectionSource, TeamRecordModel.class);
        TableUtils.createTableIfNotExists(connectionSource, TeamAbsenceModel.class);
        TableUtils.createTableIfNotExists(connectionSource, StrikeModel.class);
        TableUtils.createTableIfNotExists(connectionSource, StrikeTemplateModel.class);
        TableUtils.createTableIfNotExists(connectionSource, StrikeModel.class);
    }
}