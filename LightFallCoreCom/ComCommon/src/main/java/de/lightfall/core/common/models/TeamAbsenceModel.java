package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@DatabaseTable(tableName = "web_team_absence")
@NoArgsConstructor
public class TeamAbsenceModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true)
    private TeamRecordModel recordModel;

    @DatabaseField(canBeNull = false)
    private Date from;

    @DatabaseField(canBeNull = false)
    private Date to;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private Date created_at;
}
