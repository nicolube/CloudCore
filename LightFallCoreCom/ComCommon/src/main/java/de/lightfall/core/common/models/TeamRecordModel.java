package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<TeamAbsenceModel> teamAbsenceModels;

    @ForeignCollectionField()
    private List<StrikeModel> strikeModels;

    @DatabaseField
    private int teamJoins;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private UserInfoModel updated_by;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private  Date updated_at;

    @DatabaseField(canBeNull = false, readOnly = true, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private Date created_at;

    public TeamRecordModel(UserInfoModel userInfo) {
        this.userInfo = userInfo;
    }
}
