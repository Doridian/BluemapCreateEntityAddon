package eu.cronmoth.createentityaddon.rendering;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockStateModelRenderer;
import de.bluecolored.bluemap.core.map.hires.entity.EntityRenderer;
import de.bluecolored.bluemap.core.map.hires.entity.EntityRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.entitystate.Part;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.Entity;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import eu.cronmoth.createentityaddon.rendering.entitymodel.BlockAttribute;
import eu.cronmoth.createentityaddon.rendering.entitymodel.ContraptionEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class ContraptionEntityRenderer implements EntityRenderer {
    public static final EntityRendererType TYPE = new EntityRendererType.Impl(new Key("create","contraption"), ContraptionEntityRenderer::new);
    private final ResourcePack resourcePack;
    private final BlockStateModelRenderer blockRenderer;
    private final RenderSettings renderSettings;


    public ContraptionEntityRenderer(ResourcePack resourcePack, TextureGallery textureGallery, RenderSettings renderSettings) {
        this.resourcePack = resourcePack;
        this.blockRenderer = new BlockStateModelRenderer(resourcePack, textureGallery, renderSettings);
        this.renderSettings = renderSettings;
    }

    @Override
    public void render(Entity entity, BlockNeighborhood block, Part part, TileModelView tileModel) {
        System.out.println("inside ContraptionEntityRenderer");
        if (!(entity instanceof ContraptionEntity contraption)) {
            return;
        }
        if (contraption.isTrain()) {
            contraption.setPos(new Vector3d(0, 0, 0));
        }
        Map<Vec3, BlockAttribute> blocks = new HashMap<>();
        for (BlockAttribute nbtBlock : contraption.getContraption().getBlocks().getBlockList()) {
            long[] coords = unpackCoordinates(nbtBlock.getPosition());
            if (contraption.isTrain()) {

            }

            double x = coords[0] + contraption.getPos().getX();
            double y = coords[1] + contraption.getPos().getY();
            double z = coords[2] + contraption.getPos().getZ();

            nbtBlock.setRelativePosition(new Vec3(coords[0], coords[1], coords[2]));
            Vec3 key = new Vec3(x,y,z);
            blocks.put(key, nbtBlock);
            nbtBlock.setCoords(key);
        }
        contraption.setBlocks(blocks);
        for (BlockAttribute nbtBlock : contraption.getContraption().getBlocks().getBlockList()) {
            ContraptionBlock blockAccess = new ContraptionBlock(contraption);
            blockAccess.setBlock(nbtBlock);
            //blockAccess.set((int)nbtBlock.getCoords().x, (int)nbtBlock.getCoords().y, (int)nbtBlock.getCoords().z);
            tileModel.initialize();
            BlockNeighborhood neighborhood = new BlockNeighborhood(blockAccess, resourcePack, renderSettings,block.getDimensionType());
            blockRenderer.render(neighborhood, tileModel, new Color());
            Vec3 relativePos = nbtBlock.getRelativePosition();
            tileModel.translate((int)relativePos.x, (int)relativePos.y, (int)relativePos.z);
        }
    }

    private long[] rotateCoordinates(long [] coordinates, String direction) {
        long x = coordinates[0];
        long y = coordinates[1];
        long z = coordinates[2];
        return switch (direction.toLowerCase()) {
            case "north" -> new long[]{x, y, z};
            case "south" -> new long[]{-x, y, -z};
            case "west" -> new long[]{z, y, -x};
            case "east" -> new long[]{-z, y, x};
            default -> new long[]{x, y, z};
        };
    }

    private long[] unpackCoordinates(long pos)
    {
        long ym = 1<<12;
        long xm = 1<<26;
        long zm = 1<<26;

        long ys = (pos>>00)&(ym-1);
        long zs = (pos>>12)&(zm-1);
        long xs = (pos>>38)&(xm-1);

        long y = (ys>=ym/2)?(ys-ym):ys;
        long z = (zs>=zm/2)?(zs-zm):zs;
        long x = (xs>=xm/2)?(xs-xm):xs;

        return new long[]  {x, y, z};
    }
}
