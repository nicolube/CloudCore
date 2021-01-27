package de.cloud.core.common.models;

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

    @DatabaseField(width = 25, canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String deception;

    @DatabaseField
    private long points;

    @DatabaseField
    private long baseTime;

    @DatabaseField(canBeNull = false)
    private boolean severity;

}
