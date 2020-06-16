package de.lightfall.core.common.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.lightfall.core.api.web.TeamRecordType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@DatabaseTable(tableName = "web_strike")
@NoArgsConstructor
public class StrikeModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true)
    private TeamRecordModel recordModel;

    @DatabaseField
    private String customDescription;

    @DatabaseField
    private String customPoints;

    @DatabaseField
    private Date customEnd;

    @DatabaseField
    private TeamRecordType type;

    @DatabaseField(canBeNull = false)
    private float severity;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private StrikeTemplateModel templateModel;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private Date created_at;
}
