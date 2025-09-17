package eu.cronmoth.createentityaddon.rendering;

import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.biome.Biome;
import de.bluecolored.bluemap.core.world.block.BlockAccess;
import eu.cronmoth.createentityaddon.rendering.entitymodel.BlockAttribute;
import eu.cronmoth.createentityaddon.rendering.entitymodel.ContraptionEntity;
import lombok.Data;
import net.minecraft.world.phys.Vec3;
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
        double x = this.block.getCoords().x+this.x;
        double y = this.block.getCoords().y+this.y;
        double z = this.block.getCoords().z+this.z;
        BlockAttribute block = contraption.getBlocks().get(new Vec3(x,y,z));
        if (block == null) {
            return BlockState.AIR;
        }
        return new BlockState(contraption.getContraption().getBlocks().getPalette().get(block.getState()).getName());
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
