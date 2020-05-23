package de.lightfall.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@DatabaseTable(tableName = "messages")
@NoArgsConstructor
public class MessageModel {

    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(uniqueCombo = true, width = 150)
    private String key;
    @DatabaseField
    private String message;
    @DatabaseField(width = 7, uniqueCombo = true)
    private String locale;
    @DatabaseField
    private boolean prefix;


    public MessageModel(String key, String message, Locale locale) {
        this.key = key;
        this.message = message;
        this.locale = locale.getLanguage();
    }
}
