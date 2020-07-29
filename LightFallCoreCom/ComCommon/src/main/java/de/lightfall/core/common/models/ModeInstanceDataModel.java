package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.lightfall.core.api.stats.IModeInstanceData;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "mode_instance_data")
@NoArgsConstructor
public class ModeInstanceDataModel implements IModeInstanceData {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true)
    private ModeInstanceModel modeInstance;

    @DatabaseField(canBeNull = false, width = 25, uniqueCombo = true)
    private String name;

    @DatabaseField
    private String value;
}
