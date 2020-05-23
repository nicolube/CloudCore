package de.lightfall.core.common.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@DatabaseTable(tableName = "web_strike")
@NoArgsConstructor
public class StrikeModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private float severity;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private StrikeTemplateModel templateModel;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private Date created_at;
}
