package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.lightfall.core.api.ClientType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "inter_com_token")
@NoArgsConstructor
public class InterComTokenModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(unique = true, width = 25)
    private String comment;

    @DatabaseField
    private ClientType type;

    @DatabaseField(unique = true, width = 100)
    private String token;

}