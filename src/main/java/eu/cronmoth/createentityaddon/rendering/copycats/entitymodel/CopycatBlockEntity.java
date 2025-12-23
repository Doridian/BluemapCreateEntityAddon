package eu.cronmoth.createentityaddon.rendering.copycats.entitymodel;

import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;
import eu.cronmoth.createentityaddon.rendering.contraptions.entitymodel.MaterialData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CopycatBlockEntity extends MCABlockEntity {
    @NBTName("Material") private MaterialData material;

    public CopycatBlockEntity() {
        super();
    }
}
