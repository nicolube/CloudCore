package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.lightfall.core.api.stats.IStatUpdate;
import de.lightfall.core.common.StatUpdateType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@DatabaseTable(tableName = "stats_history")
@NoArgsConstructor
public class StatUpdateModel implements IStatUpdate {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true)
    private ModeInstanceUserModel modeInstanceUser;

    @DatabaseField(canBeNull = false, width = 25)
    private String name;

    @DatabaseField
    private long value;

    @DatabaseField
    private StatUpdateType type;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private Date created_at;
}
