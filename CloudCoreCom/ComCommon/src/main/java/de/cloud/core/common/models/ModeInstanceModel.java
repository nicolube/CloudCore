package de.cloud.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import de.cloud.core.api.stats.IModeInstance;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Data
@DatabaseTable(tableName = "mode_instance")
@NoArgsConstructor
public class ModeInstanceModel implements IModeInstance {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(width = 16)
    private String mode;

    @ForeignCollectionField
    Collection<ModeInstanceUserModel> modeInstanceUsers;

    @ForeignCollectionField
    Collection<ModeInstanceDataModel> statData;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private Date created_at;

    public ModeInstanceModel(String mode) {
        this.mode = mode;
        this.modeInstanceUsers = new ArrayList<>();
        this.statData = new ArrayList<>();
    }
}
