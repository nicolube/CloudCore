package de.lightfall.core.common.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@DatabaseTable(tableName = "mode_instance_data")
@NoArgsConstructor
public class ModeInstanceDataModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true)
    private ModeInstanceModel modeInstance;

    @DatabaseField(canBeNull = false, width = 25, uniqueCombo = true)
    private String name;

    @DatabaseField
    private String value;
}
