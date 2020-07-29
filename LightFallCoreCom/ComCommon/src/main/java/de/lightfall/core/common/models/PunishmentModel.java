package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.lightfall.core.api.punishments.IPunishment;
import de.lightfall.core.api.punishments.PunishmentType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@DatabaseTable(tableName = "punishments")
@NoArgsConstructor
public class PunishmentModel implements IPunishment {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private UserInfoModel userInfo;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private UserModeInfoModel userModeInfo;

    @DatabaseField(foreign = true)
    private UserInfoModel punished_by;

    @DatabaseField
    private PunishmentType type;

    @DatabaseField(readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date created_at;

    @DatabaseField
    private Date end;

    @DatabaseField
    private String reason;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private UserInfoModel unPunish_by;

    @DatabaseField
    private Date unPunish_at;

    @DatabaseField(width = 50)
    private String unPunish_comment;

    public PunishmentModel(UserInfoModel userInfo, UserModeInfoModel userModeInfo, UserInfoModel punished_by, PunishmentType type, Date end, String reason) {
        this.userInfo = userInfo;
        this.userModeInfo = userModeInfo;
        this.punished_by = punished_by;
        this.type = type;
        this.end = end;
        this.reason = reason;
    }

    public void unPunish(UserInfoModel sender, Date date, String comment) {
        this.unPunish_by = sender;
        this.unPunish_at = date;
        this.unPunish_comment = comment;
    }
}
