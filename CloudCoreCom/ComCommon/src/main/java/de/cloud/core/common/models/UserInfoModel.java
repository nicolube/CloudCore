package de.cloud.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.cloud.core.api.usermanager.IUserInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.UUID;

@Data
@DatabaseTable(tableName = "user_info")
@NoArgsConstructor
public class UserInfoModel implements IUserInfo {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long teamspeak_id;

    @DatabaseField
    private long forum_id;

    @DatabaseField
    private long discord_id;

    @DatabaseField
    private long ontime;

    @DatabaseField(canBeNull = false, unique = true)
    private String name;

    @DatabaseField(canBeNull = false, unique = true)
    private UUID uuid;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private PunishmentModel activeBan;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private PunishmentModel activeMute;

    @DatabaseField(canBeNull = false, width = 7)
    private String locale;

    public UserInfoModel(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.locale = Locale.ENGLISH.getLanguage();
    }
}
