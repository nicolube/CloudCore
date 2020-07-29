package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "stats")
@NoArgsConstructor
public class StatModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true, uniqueCombo = true)
    private UserInfoModel userInfo;

    @DatabaseField(width = 16, uniqueCombo = true)
    private String mode;

    @DatabaseField(canBeNull = false, width = 25, uniqueCombo = true)
    private String name;

    @DatabaseField
    private long value;


}
