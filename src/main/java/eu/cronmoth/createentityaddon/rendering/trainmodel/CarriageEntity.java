package eu.cronmoth.createentityaddon.rendering.trainmodel;

import eu.cronmoth.createentityaddon.rendering.entitymodel.ContraptionAttribute;

import java.util.List;

public class CarriageEntity {
    public List<Float> Motion;
    public boolean Initialized;
    public int CarriageIndex;
    public boolean Invulnerable;
    public short Air;
    public boolean OnGround;
    public int PortalCooldown;
    public ContraptionAttribute Contraption;
    public List<Float> Rotation;
    public String InitialOrientation;
    public float Yaw;
    public List<Float> CachedMotion;
    public float FallDistance;
    public List<Double> Pos;
    public short Fire;
    public float Pitch;
    public String id;
    public int[] UUID;
    public boolean Stalled;
    public boolean Placed;
    public int[] TrainId;
}
