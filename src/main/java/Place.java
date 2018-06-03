/**
 * Klasse zum Speichern von "Orten", diese können entweder Pickup oder Delivery Locations sein, wird hier nicht weiter unterschieden(evtl. sinnvoll?)
 */
public class Place {
    private double lat;
    private double lon;
    private String locName;
    private int locID;

    /**
     *
     * @param lat latitude des Ortes
     * @param lon longitude des Ortes
     * @param locName Name des ortes
     * @param locID ID des Ortes
     */
    public Place(double lat, double lon, String locName, int locID) {
        this.lat = lat;
        this.lon = lon;
        this.locName = locName;
        this.locID = locID;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getLocID() {
        return locID;
    }

    public String getLocName() {
        return locName;
    }

    public String toString() {
        return locName;
    }
}
