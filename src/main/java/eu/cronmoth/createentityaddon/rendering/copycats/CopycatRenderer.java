// FULL FILE WITH FIXED UV CROPPING AND BOUNDS PASSING
// (see comments marked FIX)

package eu.cronmoth.createentityaddon.rendering.copycats;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector4f;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.BlockColorCalculatorFactory;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.ResourcePool;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Face;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.util.math.MatrixM4f;
import de.bluecolored.bluemap.core.util.math.VectorM2f;
import de.bluecolored.bluemap.core.util.math.VectorM3f;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.block.ExtendedBlock;
import eu.cronmoth.createentityaddon.rendering.copycats.entitymodel.CopycatBlockEntity;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CopycatRenderer implements BlockRenderer {

    public static final BlockRendererType TYPE =
            new BlockRendererType.Impl(new Key("create", "copycat"), CopycatRenderer::new);

    private static final float BLOCK_SCALE = 1f / 16f;

    private final ResourcePack resourcePack;
    private final TextureGallery textureGallery;
    private final RenderSettings renderSettings;
    private final BlockColorCalculatorFactory.BlockColorCalculator blockColorCalculator;

    private final VectorM3f[] corners = new VectorM3f[8];
    private final VectorM2f[] rawUvs = new VectorM2f[4];
    private final VectorM2f[] uvs = new VectorM2f[4];

    private BlockNeighborhood block;
    private Variant variant;
    private Model modelResource;
    private TileModelView blockModel;
    private Color blockColor;
    private float blockColorOpacity;
    private final MatrixM4f elementTransform = new MatrixM4f();

    public CopycatRenderer(ResourcePack resourcePack, TextureGallery textureGallery, RenderSettings renderSettings) {
        this.resourcePack = resourcePack;
        this.textureGallery = textureGallery;
        this.renderSettings = renderSettings;
        this.blockColorCalculator = resourcePack.getColorCalculatorFactory().createCalculator();

        for (int i = 0; i < corners.length; i++) corners[i] = new VectorM3f(0, 0, 0);
        for (int i = 0; i < rawUvs.length; i++) rawUvs[i] = new VectorM2f(0, 0);
    }

    @Override
    public void render(BlockNeighborhood block, Variant variant, TileModelView blockModel, Color color) {
        this.block = block;
        this.variant = variant;
        this.blockModel = blockModel;
        this.blockColor = color;
        this.blockColorOpacity = 0f;
        ResourcePool<Model> models = resourcePack.getModels();
        this.modelResource = variant.getModel().getResource(models::get);

        if (!(block.getBlockEntity() instanceof CopycatBlockEntity entity)) return;
        if (modelResource == null) return;
        String half = block.getBlockState().getProperties().get("half"); //bottom or top
        String facingStr = block.getBlockState().getProperties().get("facing");
        Direction facing = Direction.fromString(facingStr);
        String[] name = entity.getMaterial().getName().split(":");
        Model copiedModel = resourcePack.getModels().get(new ResourcePath<>(name[0]+ ":block/" + name[1]));
        if (copiedModel == null) return;

        int modelStart = blockModel.getStart();

        Element[] elements = modelResource.getElements();
        if (elements != null) {
            for (Element element : elements) {
                buildModelElement(element, blockModel.initialize(), copiedModel, half, facing);
            }
        }

        if (color.a > 0) {
            color.flatten().straight();
            color.a = blockColorOpacity;
        }
        blockModel.initialize(modelStart);
        if (variant.isTransformed()) blockModel.transform(variant.getTransformMatrix());
    }

    private void buildModelElement(Element element, TileModelView model, Model copiedModel, String half, Direction facing) {
        boolean isStep = half!=null;

        Vector3f from = element.getFrom();
        Vector3f to = element.getTo();

        float minX = Math.min(from.getX(), to.getX());
        float minY = Math.min(from.getY(), to.getY());
        float minZ = Math.min(from.getZ(), to.getZ());
        float maxX = Math.max(from.getX(), to.getX());
        float maxY = Math.max(from.getY(), to.getY());
        float maxZ = Math.max(from.getZ(), to.getZ());

        // Compute cube corners
        VectorM3f[] c = corners;
        c[0].set(minX, minY, minZ);
        c[1].set(minX, minY, maxZ);
        c[2].set(maxX, minY, minZ);
        c[3].set(maxX, minY, maxZ);
        c[4].set(minX, maxY, minZ);
        c[5].set(minX, maxY, maxZ);
        c[6].set(maxX, maxY, minZ);
        c[7].set(maxX, maxY, maxZ);

        int start = model.getStart();

        // Build faces
        face(element, Direction.DOWN, c[0], c[2], c[3], c[1], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.UP,   c[5], c[7], c[6], c[4], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.NORTH,c[2], c[0], c[4], c[6], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.SOUTH,c[1], c[3], c[7], c[5], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.WEST, c[0], c[1], c[5], c[4], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.EAST, c[3], c[2], c[6], c[7], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);

        model.initialize(start);
        // Pixel offsets
        float offsetX = 0f;
        float offsetY = 0f;
        float offsetZ = (isStep) ? -4f: 0f;

        // Apply half offset (top = 8 pixels in Y)
        if ("top".equalsIgnoreCase(half)) {
            offsetY = 8f; // or Z depending on your coordinate system
        }

        // Start with element's own rotation
        MatrixM4f transform = elementTransform.copy(element.getRotation().getMatrix());

        // Apply translation in pixels
        transform.translate(offsetX, offsetY, offsetZ);

        transform.translate(-8f, -8f, -8f);

        if (facing != null) {
            switch (facing) {
                case SOUTH -> transform.rotate(180f, 0f, 1f, 0f);
                case WEST  -> transform.rotate(90f,  0f, 1f, 0f);
                case EAST  -> transform.rotate(-90f, 0f, 1f, 0f);
                default -> {}
            }

            if (!isStep) {
                switch (facing) {
                    case WEST  -> transform.rotate(90f,  0f, 0f, 1f);
                    case EAST  -> transform.rotate(-90f, 0f, 0f, 1f);
                    case NORTH -> transform.rotate(-90f, 1f, 0f, 0f);
                    case SOUTH -> transform.rotate(90f,  1f, 0f, 0f);
                    case DOWN  -> transform.translate(0f, 13f, 0f);
                    default -> {}
                }
            }
        }

        transform.translate(8f, 8f, 8f);

        transform.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);

        model.transform(transform);
    }


    private void face(
            Element element,
            Direction dir,
            VectorM3f c0, VectorM3f c1, VectorM3f c2, VectorM3f c3,
            Model copiedModel,
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ
    ) {
        Face face = element.getFaces().get(dir);
        if (face == null) return;
        ExtendedBlock facedBlockNeighbor = getRotationRelativeBlock(dir);
        LightData blockLightData = block.getLightData();
        LightData facedLightData = facedBlockNeighbor.getLightData();

        int sunLight = Math.max(blockLightData.getSkyLight(), facedLightData.getSkyLight());
        int blockLight = Math.max(blockLightData.getBlockLight(), facedLightData.getBlockLight());

        Optional<Face> mapped = Arrays.stream(copiedModel.getElements())
                .filter(Objects::nonNull)
                .map(e -> e.getFaces().get(dir))
                .filter(Objects::nonNull)
                .findFirst();
        if (mapped.isPresent()) face = mapped.get();

        Vector4f uv = face.getUv();
        float uvx = uv.getX() / 16f;
        float uvy = uv.getY() / 16f;
        float uvz = uv.getZ() / 16f;
        float uvw = uv.getW() / 16f;

        float uMin, uMax, vMin, vMax;
        switch (dir) {
            case UP -> { uMin = minX/16f; uMax = maxX/16f; vMin = minZ/16f; vMax = maxZ/16f; }
            case DOWN -> { uMin = minX/16f; uMax = maxX/16f; vMin = maxZ/16f; vMax = minZ/16f; }
            case NORTH -> { uMin = minX/16f; uMax = maxX/16f; vMin = minY/16f; vMax = maxY/16f; }
            case SOUTH -> { uMin = maxX/16f; uMax = minX/16f; vMin = minY/16f; vMax = maxY/16f; }
            case WEST -> { uMin = maxZ/16f; uMax = minZ/16f; vMin = minY/16f; vMax = maxY/16f; }
            case EAST -> { uMin = minZ/16f; uMax = maxZ/16f; vMin = minY/16f; vMax = maxY/16f; }
            default -> { uMin = 0; uMax = 1; vMin = 0; vMax = 1; }
        }

        float u0 = lerp(uvx, uvz, uMin);
        float u1 = lerp(uvx, uvz, uMax);
        float v0 = lerp(uvy, uvw, vMin);
        float v1 = lerp(uvy, uvw, vMax);

        rawUvs[0].set(u0, v1);
        rawUvs[1].set(u1, v1);
        rawUvs[2].set(u1, v0);
        rawUvs[3].set(u0, v0);

        int rot = Math.floorDiv(face.getRotation(), 90) & 3;
        for (int i = 0; i < 4; i++) uvs[i] = rawUvs[(rot + i) & 3];

        blockModel.initialize();
        blockModel.add(2);
        TileModel tileModel = blockModel.getTileModel();
        int f1 = blockModel.getStart();
        int f2 = f1 + 1;

        tileModel.setPositions(f1, c0.x,c0.y,c0.z, c1.x,c1.y,c1.z, c2.x,c2.y,c2.z);
        tileModel.setPositions(f2, c0.x,c0.y,c0.z, c2.x,c2.y,c2.z, c3.x,c3.y,c3.z);

        int tex = textureGallery.get(face.getTexture().getTexturePath(copiedModel.getTextures()::get));
        tileModel.setMaterialIndex(f1, tex);
        tileModel.setMaterialIndex(f2, tex);

        tileModel.setUvs(f1, uvs[0].x,uvs[0].y, uvs[1].x,uvs[1].y, uvs[2].x,uvs[2].y);
        tileModel.setUvs(f2, uvs[0].x,uvs[0].y, uvs[2].x,uvs[2].y, uvs[3].x,uvs[3].y);

        tileModel.setColor(f1,1,1,1);
        tileModel.setColor(f2,1,1,1);

        // ####### blocklight
        int emissiveBlockLight = Math.max(blockLight, element.getLightEmission());
        tileModel.setBlocklight(f1, emissiveBlockLight);
        tileModel.setBlocklight(f2, emissiveBlockLight);

        // ####### sunlight
        tileModel.setSunlight(f1, sunLight);
        tileModel.setSunlight(f2, sunLight);

        // ######## AO
        float ao0 = 1f, ao1 = 1f, ao2 = 1f, ao3 = 1f;
        if (modelResource.isAmbientocclusion()){
            ao0 = testAo(c0, dir);
            ao1 = testAo(c1, dir);
            ao2 = testAo(c2, dir);
            ao3 = testAo(c3, dir);
        }

        tileModel.setAOs(f1, ao0, ao1, ao2);
        tileModel.setAOs(f2, ao0, ao2, ao3);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

/////////////////////////////////////////// copied from ResourceModelRenderer.java
    private ExtendedBlock getRotationRelativeBlock(Direction direction){
        return getRotationRelativeBlock(direction.toVector());
    }

    private ExtendedBlock getRotationRelativeBlock(Vector3i direction){
        return getRotationRelativeBlock(
                direction.getX(),
                direction.getY(),
                direction.getZ()
        );
    }

    private final VectorM3f rotationRelativeBlockDirection = new VectorM3f(0, 0, 0);
    private ExtendedBlock getRotationRelativeBlock(int dx, int dy, int dz){
        rotationRelativeBlockDirection.set(dx, dy, dz);
        makeRotationRelative(rotationRelativeBlockDirection);

        return block.getNeighborBlock(
                Math.round(rotationRelativeBlockDirection.x),
                Math.round(rotationRelativeBlockDirection.y),
                Math.round(rotationRelativeBlockDirection.z)
        );
    }

    private void makeRotationRelative(VectorM3f direction){
        if (variant.isTransformed())
            direction.rotateAndScale(variant.getTransformMatrix());
    }

    private float testAo(VectorM3f vertex, Direction dir){
        Vector3i dirVec = dir.toVector();
        int occluding = 0;

        int x = 0;
        if (vertex.x == 16){
            x = 1;
        } else if (vertex.x == 0){
            x = -1;
        }

        int y = 0;
        if (vertex.y == 16){
            y = 1;
        } else if (vertex.y == 0){
            y = -1;
        }

        int z = 0;
        if (vertex.z == 16){
            z = 1;
        } else if (vertex.z == 0){
            z = -1;
        }


        if (x * dirVec.getX() + y * dirVec.getY() > 0){
            if (getRotationRelativeBlock(x, y, 0).getProperties().isOccluding()) occluding++;
        }

        if (x * dirVec.getX() + z * dirVec.getZ() > 0){
            if (getRotationRelativeBlock(x, 0, z).getProperties().isOccluding()) occluding++;
        }

        if (y * dirVec.getY() + z * dirVec.getZ() > 0){
            if (getRotationRelativeBlock(0, y, z).getProperties().isOccluding()) occluding++;
        }

        if (x * dirVec.getX() + y * dirVec.getY() + z * dirVec.getZ() > 0){
            if (getRotationRelativeBlock(x, y, z).getProperties().isOccluding()) occluding++;
        }

        if (occluding > 3) occluding = 3;
        return  Math.max(0f, Math.min(1f - occluding * 0.25f, 1f));
    }
}
