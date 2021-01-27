package de.cloud.core.api.channelhandeler.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDocument extends Document {

    private String world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public LocationDocument(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
