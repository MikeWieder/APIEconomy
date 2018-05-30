public class VehicleDefinition {

    private String id;
    private int capacity;
    private Place startLocation;

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
