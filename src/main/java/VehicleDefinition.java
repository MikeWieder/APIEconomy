/**
 * Klasse zum Darstellen eines Fahrzeugs
 */
public class VehicleDefinition {

    private String id;
    private int capacity;
    private Place startLocation;

    /**
     *
     * @param id ID bzw. Name des zu erzeugenden Fahrzeugs
     * @param capacity Kapazität des Fahrzeugs
     * @param startLocation Startpunkt des Fahrzeugs, muss in der CostMatrix enthalten sein.
     */
    public VehicleDefinition(String id, int capacity, Place startLocation) {
        this.capacity = capacity;
        this.id = id;
        this.startLocation = startLocation;
    }

    public String getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public Place getStartLocation() {
        return startLocation;
    }
}
