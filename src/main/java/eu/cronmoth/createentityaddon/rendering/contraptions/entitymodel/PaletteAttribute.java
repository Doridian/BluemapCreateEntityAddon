package eu.cronmoth.createentityaddon.rendering.contraptions.entitymodel;

import de.bluecolored.bluenbt.NBTName;
import lombok.Data;

import java.util.Map;

@Data
public class PaletteAttribute {
    @NBTName("Name")
    private String name;
    @NBTName("Properties")
    private Map<String,String> properties;
}
