package de.lightfall.core.common.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@DatabaseTable(tableName = "mode_instance_user")
@NoArgsConstructor
public class ModeInstanceUserModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true)
    private ModeInstanceModel modeInstance;

    @ForeignCollectionField
    ForeignCollection<StatUpdateModel> statUpdates;
}
