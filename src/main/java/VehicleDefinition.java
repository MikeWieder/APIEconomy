/**
 * Klasse zum Darstellen eines Fahrzeugs
 */
public class VehicleDefinition {

    private int id;
    private int capacity;
    private Place startLocation;
    private String name;

    /**
     *
     * @param id ID bzw. Name des zu erzeugenden Fahrzeugs
     * @param capacity Kapazität des Fahrzeugs
     * @param startLocation Startpunkt des Fahrzeugs, muss in der CostMatrix enthalten sein.
     */
    public VehicleDefinition(int id, int capacity, Place startLocation, String name) {
        this.capacity = capacity;
        this.id = id;
        this.startLocation = startLocation;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public Place getStartLocation() {
        return startLocation;
    }

    public String getName() {
        return name;
    }
}
