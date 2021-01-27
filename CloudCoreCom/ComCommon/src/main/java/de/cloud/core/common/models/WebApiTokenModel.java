package de.cloud.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "web_api_token")
@NoArgsConstructor
public class WebApiTokenModel {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(unique = true, width = 25)
    private String name;

    @DatabaseField(unique = true, width = 100)
    private String token;

    public WebApiTokenModel(String name, String token) {
        this.name = name;
        this.token = token;
    }
}
