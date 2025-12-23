package eu.cronmoth.createentityaddon.rendering.contraptions.entitymodel;

import de.bluecolored.bluenbt.NBTName;
import lombok.Data;

@Data
public class BlockDataAttribute {
        @NBTName("Item")
        private ItemData item;

        @NBTName("Material")
        private MaterialData material;

        @NBTName("id")
        private String id; // e.g. "create:copycat"

}
