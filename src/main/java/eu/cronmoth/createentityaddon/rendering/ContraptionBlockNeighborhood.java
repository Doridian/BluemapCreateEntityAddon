package eu.cronmoth.createentityaddon.rendering;

import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.world.DimensionType;
import de.bluecolored.bluemap.core.world.block.BlockAccess;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;

public class ContraptionBlockNeighborhood extends BlockNeighborhood {
    public ContraptionBlockNeighborhood(BlockAccess blockAccess, ResourcePack resourcePack, RenderSettings renderSettings, DimensionType dimensionType) {
        super(blockAccess, resourcePack, renderSettings, dimensionType);
    }

    @Override
    public boolean isInsideRenderBounds() {
        return true;
    }
}
