package eu.cronmoth.createentityaddon.rendering.entitymodel;

import de.bluecolored.bluenbt.NBTName;
import lombok.Data;

@Data
public class ContraptionAttribute {
    @NBTName("Blocks")
    private BlocksAttribute blocks ;
    @NBTName("AssemblyDirection") private String assemblyDirection;
}
