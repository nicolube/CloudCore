package de.cloud.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import de.cloud.core.api.stats.IModeInstanceUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@DatabaseTable(tableName = "mode_instance_user")
@NoArgsConstructor
public class ModeInstanceUserModel implements IModeInstanceUser {

    @DatabaseField(generatedId = true)
    private long id;


    @DatabaseField(canBeNull = false, foreign = true, uniqueCombo = true)
    private UserInfoModel userInfo;

    @DatabaseField(canBeNull = false, foreign = true, uniqueCombo = true)
    private ModeInstanceModel modeInstance;

    @ForeignCollectionField
    Collection<StatUpdateModel> statUpdates;
}
