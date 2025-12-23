package eu.cronmoth.createentityaddon.rendering.contraptions.trainmodel;


import java.util.Map;

public class Carriage {
    public CarriageEntity Entity;
    public boolean FrontConductor;
    public Map<String, Object> Passengers;
    public boolean BackConductor;
    public int Spacing;
    public Bogey SecondBogey;
    public boolean Stalled;
    //public List<EntityPositioning> EntityPositioning;
    public Bogey FirstBogey;
}
