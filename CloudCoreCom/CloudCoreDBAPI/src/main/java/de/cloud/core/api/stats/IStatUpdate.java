package de.cloud.core.api.stats;


import java.util.Date;

public interface IStatUpdate {

    long getId();

    IModeInstanceUser getModeInstanceUser();

    String getName();

    long getValue();

    StatUpdateType getType();

    Date getCreated_at();
}
