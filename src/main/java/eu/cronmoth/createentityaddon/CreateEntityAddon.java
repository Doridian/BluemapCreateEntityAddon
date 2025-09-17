package eu.cronmoth.createentityaddon;


import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.common.BlueMapService;
import de.bluecolored.bluemap.common.api.BlueMapAPIImpl;
import de.bluecolored.bluemap.core.map.BmMap;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.*;
import de.bluecolored.bluemap.core.map.hires.entity.EntityRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.mca.entity.EntityType;
import de.bluecolored.bluenbt.BlueNBT;
import eu.cronmoth.createentityaddon.rendering.ContraptionBlock;
import eu.cronmoth.createentityaddon.rendering.entitymodel.ContraptionAttribute;
import eu.cronmoth.createentityaddon.rendering.entitymodel.ContraptionEntity;
import eu.cronmoth.createentityaddon.rendering.ContraptionEntityRenderer;
import eu.cronmoth.createentityaddon.rendering.trainmodel.Carriage;
import eu.cronmoth.createentityaddon.rendering.trainmodel.TrainRoot;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;


public class CreateEntityAddon implements Runnable {
    public static final Logger LOGGER = LogUtils.getLogger();


    private void addBluemapRegistryValues() {
        EntityRendererType.REGISTRY.register(ContraptionEntityRenderer.TYPE);
        EntityType.REGISTRY.register(new EntityType.Impl(new Key("create", "stationary_contraption"), ContraptionEntity.class));
    }

    private void loadTrainModels(BlueMapAPI api) {
        BlueNBT blueNBT = new BlueNBT();
        String workingDir = System.getProperty("user.dir");
        BlueMapService service = ((BlueMapAPIImpl) api).blueMapService();

        BmMap map = service.getMaps().values().stream().findFirst().orElse(null);
        try (
                InputStream in = Files.newInputStream(Path.of(workingDir + "/"+ map.getWorld().getName() + "/data/create_tracks.dat"));
                InputStream compressedIn = new BufferedInputStream(new GZIPInputStream(in))
        ) {
            TrainRoot networkData = blueNBT.read(compressedIn, TrainRoot.class);
            deleteDirectory(new File(workingDir + "/bluemap/train_models"));
            networkData.data.Trains.forEach(train -> {
                UUID uuid = IntArrayToUUID(train.Id);
                int i = 0;
                for (Carriage carriage : train.Carriages) {
                    ContraptionEntity entity = new ContraptionEntity();
                    ContraptionAttribute contraption = carriage.Entity.Contraption;
                    entity.setId(new Key(carriage.Entity.id));
                    entity.setUuid(IntArrayToUUID(carriage.Entity.UUID));
                    List<Double> posList = carriage.Entity.Pos;
                    entity.setPos(new Vector3d(posList.get(0), posList.get(1), posList.get(2)));
                    entity.setContraption(contraption);
                    entity.setAssemblyDirection(carriage.Entity.Contraption.getAssemblyDirection());
                    entity.setTrain(true);
                    String path = workingDir + "/bluemap/train_models/" + uuid + "_" + i + ".prbm";
                    saveTrainModel(map, entity, path);
                    i++;
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveTrainModel(BmMap map, ContraptionEntity entity, String path) {

        ResourcePack resourcePack = map.getResourcePack();
        TextureGallery textureGallery = map.getTextureGallery();
        RenderSettings renderSettings = map.getMapSettings();

        BlockNeighborhood block = new BlockNeighborhood(new ContraptionBlock(null), resourcePack, renderSettings, map.getWorld().getDimensionType());
        ContraptionEntityRenderer renderer = new ContraptionEntityRenderer(resourcePack, textureGallery, renderSettings);

        ArrayTileModel arrayTileModel = new ArrayTileModel(100);
        TileModelView tileModelView = new TileModelView(arrayTileModel);
        renderer.render(entity, block, null, tileModelView);

        Path filePath = Path.of(path);
        try {
            Files.createDirectories(filePath.getParent());
            try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                new PRBMWriter(outputStream).write((ArrayTileModel)tileModelView.getTileModel());

            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write", e);
        }
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        if (!directoryToBeDeleted.exists()) {
            return;
        }
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
    }


    public UUID IntArrayToUUID(int[] array) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        for (int i : array) {
            bb.putInt(i);
        }
        bb.flip();

        // Extract two longs (UUID is two 64-bit values)
        long high = bb.getLong();
        long low = bb.getLong();

        return new UUID(high, low);
    }

    @Override
    public void run() {
        addBluemapRegistryValues();
        BlueMapAPI.onEnable(this::loadTrainModels);
    }
}
