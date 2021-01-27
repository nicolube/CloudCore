package de.cloud.core.web.entity;

import de.cloud.core.common.models.UserInfoModel;
import lombok.Data;

@Data
public class AdvancedUserInfoEntity {

    private UserInfoModel userInfo;
    private String groupName;
    private String groupDisplayName;
    private int weight;

    public AdvancedUserInfoEntity(UserInfoModel userInfo, String groupName, String groupDisplayName, int weight) {
        this.userInfo = userInfo;
        this.groupName = groupName;
        this.groupDisplayName = groupDisplayName;
        this.weight = weight;
    }
}
