import com.j256.ormlite.dao.Dao;
import de.lightfall.core.api.config.DatabaseConfig;
import de.lightfall.core.api.punishments.PunishmentType;
import de.lightfall.core.common.DatabaseProvider;
import de.lightfall.core.common.models.PunishmentModel;
import de.lightfall.core.common.models.UserInfoModel;
import de.lightfall.core.common.models.UserModeInfoModel;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class DatabaseTest {

    private DatabaseProvider connect() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setUrl("jdbc:h2:mem:develop");
        databaseConfig.setUser("develop");
        databaseConfig.setPassword("develop");
        return new DatabaseProvider(databaseConfig);

    }

    @SneakyThrows
    //@Test
    public void testUserModel() {
        DatabaseProvider provider = connect();
        Dao<UserInfoModel, Long> uid = provider.getUserInfoDao();
        Dao<UserModeInfoModel, Long> umid = provider.getUserModeInfoDao();
        Dao<PunishmentModel, Long> pd = provider.getPunishmentDao();
        UserInfoModel uim1 = new UserInfoModel(UUID.randomUUID());
        uim1.setDiscord_id(1);
        uim1.setTeamspeak_id(1);
        uim1.setForum_id(1);
        UserInfoModel quim1 = uid.createIfNotExists(uim1);
        UserInfoModel uim2 = new UserInfoModel(UUID.randomUUID());
        uim2.setDiscord_id(2);
        uim2.setTeamspeak_id(2);
        uim2.setForum_id(2);
        UserInfoModel quim2 = uid.createIfNotExists(uim2);
        UserModeInfoModel umim = new UserModeInfoModel(quim1, "test");
        UserModeInfoModel qumim = umid.createIfNotExists(umim);
        PunishmentModel pm1 = new PunishmentModel(quim1, null, quim2, PunishmentType.BAN, null, "test1");
        pd.create(pm1);
        quim1.setActiveBan(pm1);
        uid.update(quim1);
        PunishmentModel pm2 = new PunishmentModel(quim1, qumim, quim2, PunishmentType.BAN, null, "test2");
        pd.create(pm2);
        qumim.setActiveBan(pm1);
        umid.update(qumim);
        UserInfoModel fquim = uid.queryForSameId(quim1);
        UserModeInfoModel fqumim = umid.queryForSameId(qumim);
        Assert.assertNotNull(fquim.getActiveBan());
        Assert.assertNotNull(fqumim.getActiveBan());
    }
}
