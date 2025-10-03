package eu.cronmoth.createentityaddon;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.common.BlueMapService;
import de.bluecolored.bluemap.common.api.BlueMapAPIImpl;
import de.bluecolored.bluemap.core.map.BmMap;
import de.bluecolored.bluemap.core.map.hires.entity.EntityRendererType;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.DimensionType;
import de.bluecolored.bluemap.core.world.mca.entity.EntityType;
import eu.cronmoth.createentityaddon.rendering.entitymodel.ContraptionEntity;
import eu.cronmoth.createentityaddon.rendering.ContraptionEntityRenderer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class CreateEntityAddon implements Runnable {
    List<FileWatcher> fileWatchers;

    private void addBluemapRegistryValues() {
        EntityRendererType.REGISTRY.register(ContraptionEntityRenderer.TYPE);
        EntityType.REGISTRY.register(new EntityType.Impl(new Key("create", "stationary_contraption"), ContraptionEntity.class));
    }

    @Override
    public void run() {
        addBluemapRegistryValues();
        fileWatchers = new ArrayList<>();
        BlueMapAPI.onEnable(api ->
        {
            String workingDir = System.getProperty("user.dir");
            BlueMapService service = ((BlueMapAPIImpl) api).blueMapService();
            for (BmMap map : service.getMaps().values()) {
                File dataDirectory = new File(workingDir + "/"+ map.getWorld().getName() + "/data");
                DimensionType dim = map.getWorld().getDimensionType();
                if (dataDirectory.exists() && dim.isNatural() && dim.hasSkylight() && !dim.hasCeiling()) {
                    FileWatcher fileWatcher = new FileWatcher(new File(workingDir + "/"+ map.getWorld().getName() + "/data/create_tracks.dat"), map);
                    fileWatcher.start();
                    fileWatchers.add(fileWatcher);
                }
            }
        });
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        if (directoryToBeDeleted == null || !directoryToBeDeleted.exists()) {
            return;
        }
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
