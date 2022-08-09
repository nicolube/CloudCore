package de.cloud.core.common.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.cloud.core.api.usermanager.IUserInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.UUID;

@DatabaseTable(tableName = "user_info")
@NoArgsConstructor
public class UserInfoModel implements IUserInfo {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long teamspeak_id;

    @DatabaseField
    private long forum_id;

    @DatabaseField
    private long discord_id;

    @DatabaseField
    private long ontime;

    @DatabaseField(canBeNull = false, unique = true)
    private String name;

    @DatabaseField(canBeNull = false, unique = true)
    private UUID uuid;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private PunishmentModel activeBan;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private PunishmentModel activeMute;

    @DatabaseField(canBeNull = false, width = 7)
    private String locale;

    public UserInfoModel(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.locale = Locale.ENGLISH.getLanguage();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getTeamspeak_id() {
        return teamspeak_id;
    }

    public long getForum_id() {
        return forum_id;
    }

    @Override
    public long getDiscord_id() {
        return discord_id;
    }

    @Override
    public long getOntime() {
        return ontime;
    }

    public String getName() {
        return name;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public PunishmentModel getActiveBan() {
        return activeBan;
    }

    @Override
    public PunishmentModel getActiveMute() {
        return activeMute;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTeamspeak_id(long teamspeak_id) {
        this.teamspeak_id = teamspeak_id;
    }

    public void setForum_id(long forum_id) {
        this.forum_id = forum_id;
    }

    public void setDiscord_id(long discord_id) {
        this.discord_id = discord_id;
    }

    public void setOntime(long ontime) {
        this.ontime = ontime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setActiveBan(PunishmentModel activeBan) {
        this.activeBan = activeBan;
    }

    public void setActiveMute(PunishmentModel activeMute) {
        this.activeMute = activeMute;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
