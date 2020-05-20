package de.lightfall.core.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "web_team_absence")
@NoArgsConstructor
public class TeamAbsenceModel {

    @DatabaseField(generatedId = true)
    private long id;

}
