package eu.cronmoth.createentityaddon.rendering;

import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.Entity;
import de.bluecolored.bluemap.core.world.mca.entity.EntityType;
import eu.cronmoth.createentityaddon.rendering.entitymodel.ContraptionEntity;

public class ContraptionEntityType implements EntityType {
    @Override
    public Class<? extends Entity> getEntityClass() {
        return ContraptionEntity.class;
    }

    @Override
    public Key getKey() {
        return null;
    }
}
