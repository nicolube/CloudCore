package de.lightfall.core.api.stats;

import com.j256.ormlite.field.DatabaseField;

public interface IModeInstanceData {

    long getId();

    IModeInstance getModeInstance();

    String getName();

    String getValue();
}
