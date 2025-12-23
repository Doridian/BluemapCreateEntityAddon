package eu.cronmoth.createentityaddon.rendering.contraptions.entitymodel;

import de.bluecolored.bluenbt.NBTName;
import lombok.Data;

import java.util.Map;

@Data
public class MaterialData {
    @NBTName("Name")
    private String name; // e.g. "minecraft:glass_pane"

    @NBTName("Properties")
    private Map<String, String> properties; // east, west, north, etc.
}
