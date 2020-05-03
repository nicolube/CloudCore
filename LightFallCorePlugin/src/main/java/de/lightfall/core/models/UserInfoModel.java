package de.lightfall.core.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.UUID;

@Data
@DatabaseTable(tableName = "player_info")
@NoArgsConstructor
public class UserInfoModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, unique = true)
    private UUID uuid;

    @DatabaseField(foreign = true)
    private PunishmentModel activeBan;

    @DatabaseField(foreign = true)
    private PunishmentModel activeMute;

    @DatabaseField(canBeNull = false, width = 7)
    private String locale;

    public UserInfoModel(UUID uuid) {
        this.uuid = uuid;
        this.locale = Locale.ENGLISH.getLanguage();
    }
}
