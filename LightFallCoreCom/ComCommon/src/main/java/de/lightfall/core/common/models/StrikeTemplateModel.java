package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "web_strike_template")
@NoArgsConstructor
public class StrikeTemplateModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long points;

    @DatabaseField
    private long baseTime;

    @DatabaseField(canBeNull = false)
    private boolean severity;

}
