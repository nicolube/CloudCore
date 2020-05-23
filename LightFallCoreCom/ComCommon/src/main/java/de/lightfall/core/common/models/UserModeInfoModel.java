package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.lightfall.core.api.punishments.IUserModeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "user_mode_info")
@NoArgsConstructor
public class UserModeInfoModel implements IUserModeInfo {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true, uniqueCombo = true)
    private UserInfoModel userInfo;

    @DatabaseField(width = 16, uniqueCombo = true)
    private String mode;

    @DatabaseField(foreign = true)
    private PunishmentModel activeBan;

    @DatabaseField(foreign = true)
    private PunishmentModel activeMute;

    public UserModeInfoModel(UserInfoModel userInfo, String mode) {
        this.userInfo = userInfo;
        this.mode = mode;
    }


}
