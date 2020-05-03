package de.lightfall.core.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "player_mode_info")
@NoArgsConstructor
public class UserModeInfoModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true, uniqueCombo = true)
    private UserInfoModel playerInfo;

    @DatabaseField(width = 16, uniqueCombo = true)
    private String mode;

    @DatabaseField(foreign = true)
    private PunishmentModel activeBan;

    @DatabaseField(foreign = true)
    private PunishmentModel activeMute;

    public UserModeInfoModel(UserInfoModel playerInfo, String mode) {
        this.playerInfo = playerInfo;
        this.mode = mode;
    }


}
