/**
 * Klasse zum Darstellen eines Fahrzeugs
 */
public class VehicleDefinition {

    private int id;
    private int capacity;
    private Place startLocation;
    private String uniqueName;

    /**
     *
     * @param uniqueName ID bzw. Name des zu erzeugenden Fahrzeugs; von Client vorgegeben
     * @param capacity Kapazität des Fahrzeugs
     * @param startLocation Startpunkt des Fahrzeugs, muss in der CostMatrix enthalten sein.
     */
    public VehicleDefinition(String uniqueName, int capacity, Place startLocation) {
        this.uniqueName = uniqueName;
        this.capacity = capacity;
        this.startLocation = startLocation;
    }

    public int getCapacity() {
        return capacity;
    }

    public Place getStartLocation() {
        return startLocation;
    }

    public String getName() {
        return uniqueName;
    }

    @Override
    public String toString (){
        return "Fahrzeug "+ uniqueName + " geparkt in " + startLocation.toString();
    }
}
