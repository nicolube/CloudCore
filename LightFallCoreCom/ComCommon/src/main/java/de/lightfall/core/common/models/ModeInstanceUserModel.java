package de.lightfall.core.common.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import de.lightfall.core.api.stats.IModeInstanceUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "mode_instance_user")
@NoArgsConstructor
public class ModeInstanceUserModel implements IModeInstanceUser {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true)
    private ModeInstanceModel modeInstance;

    @ForeignCollectionField
    ForeignCollection<StatUpdateModel> statUpdates;
}
