package eu.cronmoth.createentityaddon.rendering.contraptions.entitymodel;

import de.bluecolored.bluenbt.NBTName;
import lombok.Data;

@Data
class ItemData {
    @NBTName("id")
    private String id;

    @NBTName("Count")
    private byte count;
}
