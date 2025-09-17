package eu.cronmoth.createtrainwebapi.rendering.entitymodel;

import de.bluecolored.bluenbt.NBTName;
import lombok.Data;

@Data
public class PaletteAttribute {
    @NBTName("Name")
    private String name;
}
