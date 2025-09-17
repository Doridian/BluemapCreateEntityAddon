package eu.cronmoth.createentityaddon.rendering.entitymodel;

import de.bluecolored.bluenbt.NBTName;
import lombok.Data;

import java.util.List;
@Data
public class BlocksAttribute {
    @NBTName("BlockList")
    private List<BlockAttribute> blockList;
    @NBTName("Palette")
    private List<PaletteAttribute> palette;
}
