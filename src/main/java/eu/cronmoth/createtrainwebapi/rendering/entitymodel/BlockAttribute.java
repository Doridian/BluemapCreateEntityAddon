package eu.cronmoth.createtrainwebapi.rendering.entitymodel;

import de.bluecolored.bluenbt.NBTName;
import lombok.Data;
import net.minecraft.world.phys.Vec3;

@Data
public class BlockAttribute {
    @NBTName("Pos") long position;
    @NBTName("State") int state;
    private Vec3 coords;
    private Vec3 relativePosition;
}
