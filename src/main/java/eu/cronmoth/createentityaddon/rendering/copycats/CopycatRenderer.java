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
    private final Color tintColor = new Color();
    private final Color mapColor = new Color();

    private BlockNeighborhood block;
    private Variant variant;
    private Model modelResource;
    private TileModelView blockModel;
    private Color blockColor;
    private float blockColorOpacity;

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
        this.modelResource = variant.getModel().getResource(resourcePack::getModel);

        if (!(block.getBlockEntity() instanceof CopycatBlockEntity entity)) return;
        if (modelResource == null) return;

        String[] name = entity.getMaterial().getName().split(":");
        Model copiedModel = resourcePack.getModel(new ResourcePath<>("block/" + name[1]));
        if (copiedModel == null) return;

        tintColor.set(0, 0, 0, -1, true);

        int modelStart = blockModel.getStart();
        Element[] elements = modelResource.getElements();
        if (elements != null) {
            for (Element element : elements) {
                buildModelElement(element, blockModel.initialize(), copiedModel);
            }
        }

        if (color.a > 0) {
            color.flatten().straight();
            color.a = blockColorOpacity;
        }

        blockModel.initialize(modelStart);
        if (variant.isTransformed()) blockModel.transform(variant.getTransformMatrix());
    }

    private final MatrixM4f elementTransform = new MatrixM4f();

    private void buildModelElement(Element element, TileModelView model, Model copiedModel) {
        Vector3f from = element.getFrom();
        Vector3f to = element.getTo();

        float minX = Math.min(from.getX(), to.getX());
        float minY = Math.min(from.getY(), to.getY());
        float minZ = Math.min(from.getZ(), to.getZ());
        float maxX = Math.max(from.getX(), to.getX());
        float maxY = Math.max(from.getY(), to.getY());
        float maxZ = Math.max(from.getZ(), to.getZ());

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
        face(element, Direction.DOWN, c[0], c[2], c[3], c[1], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.UP,   c[5], c[7], c[6], c[4], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.NORTH,c[2], c[0], c[4], c[6], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.SOUTH,c[1], c[3], c[7], c[5], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.WEST, c[0], c[1], c[5], c[4], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);
        face(element, Direction.EAST, c[3], c[2], c[6], c[7], copiedModel, minX, minY, minZ, maxX, maxY, maxZ);

        model.initialize(start);
        model.transform(elementTransform.copy(element.getRotation().getMatrix())
                .scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE));
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
        TileModel t = blockModel.getTileModel();
        int f1 = blockModel.getStart();
        int f2 = f1 + 1;

        t.setPositions(f1, c0.x,c0.y,c0.z, c1.x,c1.y,c1.z, c2.x,c2.y,c2.z);
        t.setPositions(f2, c0.x,c0.y,c0.z, c2.x,c2.y,c2.z, c3.x,c3.y,c3.z);

        int tex = textureGallery.get(face.getTexture().getTexturePath(copiedModel.getTextures()::get));
        t.setMaterialIndex(f1, tex);
        t.setMaterialIndex(f2, tex);

        t.setUvs(f1, uvs[0].x,uvs[0].y, uvs[1].x,uvs[1].y, uvs[2].x,uvs[2].y);
        t.setUvs(f2, uvs[0].x,uvs[0].y, uvs[2].x,uvs[2].y, uvs[3].x,uvs[3].y);

        t.setColor(f1,1,1,1);
        t.setColor(f2,1,1,1);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
