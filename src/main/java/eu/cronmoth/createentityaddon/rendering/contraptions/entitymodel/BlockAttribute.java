package eu.cronmoth.createentityaddon.rendering.contraptions.entitymodel;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluenbt.NBTName;
import lombok.Data;

@Data
public class BlockAttribute {
    @NBTName("Pos") long position;
    @NBTName("State") int state;
    @NBTName("Data")
    private BlockDataAttribute data;
    private Vector3d coords;
    private Vector3d relativePosition;
}
