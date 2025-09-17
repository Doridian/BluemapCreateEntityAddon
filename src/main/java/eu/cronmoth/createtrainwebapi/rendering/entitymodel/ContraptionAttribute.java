package eu.cronmoth.createtrainwebapi.rendering.entitymodel;

import de.bluecolored.bluenbt.NBTName;
import lombok.Data;

import java.util.List;

@Data
public class ContraptionAttribute {
    @NBTName("Blocks")
    private BlocksAttribute blocks ;
    @NBTName("AssemblyDirection") private String assemblyDirection;
}
