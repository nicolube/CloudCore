package de.lightfall.core.models;

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

    @DatabaseField(canBeNull = false, foreign = true)
    private UserInfoModel playerInfo;

    @DatabaseField(foreign = true)
    private UserModeInfoModel playerModeInfo;

    @DatabaseField
    private PunishmentType type;

    @DatabaseField(columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private Date created_at;

    @DatabaseField
    private Date end;

    @DatabaseField
    private String reason;
}
