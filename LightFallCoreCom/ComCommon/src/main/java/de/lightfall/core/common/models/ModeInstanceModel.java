package de.lightfall.core.common.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@DatabaseTable(tableName = "mode_instance")
@NoArgsConstructor
public class ModeInstanceModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(width = 16)
    private String mode;

    @ForeignCollectionField
    ForeignCollection<ModeInstanceUserModel> modeInstanceUsers;

    @ForeignCollectionField
    ForeignCollection<ModeInstanceDataModel> statDate;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private Date created_at;
}
