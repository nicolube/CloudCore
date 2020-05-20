package de.lightfall.core.models;

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

}
