package eu.cronmoth.createtrainwebapi.rendering.trainmodel;

import eu.cronmoth.createtrainwebapi.rendering.*;

import java.util.List;

public class Train {
    public List<Carriage> Carriages;
    public boolean Backwards;
    public List<Object> ReservedSignalBlocks;
    public double Speed;
    public int[] Owner;
    public boolean DoubleEnded;
    //public Navigation Navigation;
    public boolean Derailed;
    public List<Object> OccupiedObservers;
    public double TargetSpeed;
    public String IconType;
    public double Throttle;
    public String Name;
    public boolean UpdateSignals;
    public int[] Station;
    //public TrainRuntime Runtime;
    public int[] Graph;
    public int MapColorIndex;
    public List<Object> MigratingPoints;
    public int Fuel;
    public int[] Id;
    public int[] CarriageSpacing;
    //public List<SignalBlockRef> SignalBlocks;
}
