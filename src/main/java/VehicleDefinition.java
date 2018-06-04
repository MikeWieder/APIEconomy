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
     * @param name ID bzw. Name des zu erzeugenden Fahrzeugs; von Client vorgegeben
     * @param capacity Kapazität des Fahrzeugs
     * @param startLocation Startpunkt des Fahrzeugs, muss in der CostMatrix enthalten sein.
     */
    public VehicleDefinition(String name, int capacity, Place startLocation) {
        //TODO: (04.06.2018, MFO) Change definition / rename "name" to "vehicleID" to fit the JSON input file
        this.name = name;
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
        return name;
    }

    @Override
    public String toString (){
        return "Fahrzeug "+ name + " geparkt in " + startLocation.toString();
    }
}
