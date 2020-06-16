package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import de.lightfall.core.api.web.TeamRecordType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@DatabaseTable(tableName = "web_team_record")
@NoArgsConstructor
public class TeamRecordModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private UserInfoModel userInfo;

    @ForeignCollectionField()
    private Collection<TeamAbsenceModel> teamAbsenceModels;

    @ForeignCollectionField()
    private Collection<StrikeModel> strikeModels;

    @DatabaseField
    private int teamJoins;

    @DatabaseField
    private int applyBlocks;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private UserInfoModel updated_by;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private  Date updated_at;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private Date created_at;

    public TeamRecordModel(UserInfoModel userInfo, UserInfoModel updated_by) {
        this.userInfo = userInfo;
        this.updated_by = updated_by;
    }
}
