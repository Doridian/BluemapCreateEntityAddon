package eu.cronmoth.createentityaddon.rendering.copycats;

import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import eu.cronmoth.createentityaddon.rendering.copycats.entitymodel.CopycatBlockEntity;

public class CopycatBlockType implements BlockEntityType {
    @Override
    public Class<? extends BlockEntity> getBlockEntityClass() {
        return CopycatBlockEntity.class;
    }

    @Override
    public Key getKey() {
        return null;
    }
}
