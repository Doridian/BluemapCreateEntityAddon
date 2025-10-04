package eu.cronmoth.createentityaddon.rendering;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.biome.Biome;
import de.bluecolored.bluemap.core.world.block.BlockAccess;
import eu.cronmoth.createentityaddon.rendering.entitymodel.BlockAttribute;
import eu.cronmoth.createentityaddon.rendering.entitymodel.ContraptionEntity;
import eu.cronmoth.createentityaddon.rendering.entitymodel.PaletteAttribute;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class ContraptionBlock implements BlockAccess {
    private ContraptionEntity contraption;
    private BlockAttribute block;

    private int x;
    private int y;
    private int z;

    public ContraptionBlock(ContraptionEntity contraption) {
        this.contraption = contraption;
    }

    @Override
    public LightData getLightData() {
        return new LightData(15, 0);
    }

    @Override
    public Biome getBiome() {
        return Biome.DEFAULT;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        return null;
    }

    @Override
    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public BlockAccess copy() {
        ContraptionBlock copy = new ContraptionBlock(contraption);
        copy.x = this.x;
        copy.y = this.y;
        copy.z = this.z;
        copy.block = this.block;
        return copy;
    }

    @Override
    public BlockState getBlockState() {
        double x = this.block.getCoords().getX()+this.x;
        double y = this.block.getCoords().getY()+this.y;
        double z = this.block.getCoords().getZ()+this.z;
        BlockAttribute block = contraption.getBlocks().get(new Vector3d(x,y,z));
        if (block == null) {
            return BlockState.AIR;
        }
        PaletteAttribute palette = contraption.getContraption().getBlocks().getPalette().get(block.getState());
        if (palette.getProperties()==null) {
            return new BlockState(new Key(palette.getName()));
        }
        return new BlockState(new Key(palette.getName()), palette.getProperties());
    }

    @Override
    public boolean hasOceanFloorY() {
        return false;
    }

    @Override
    public int getOceanFloorY() {
        return 0;
    }


}
