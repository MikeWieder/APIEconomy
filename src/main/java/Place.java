/**
 * Klasse zum Speichern von "Orten", diese können entweder Pickup oder Delivery Locations sein, wird hier nicht weiter unterschieden(evtl. sinnvoll?)
 */
public class Place {
    private double lat;
    private double lon;
    private String locName;

    /**
     *
     * @param lat latitude des Ortes
     * @param lon longitude des Ortes
     * @param locName Name des ortes
     */
    public Place(double lat, double lon, String locName) {
        this.lat = lat;
        this.lon = lon;
        this.locName = locName;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String toString() {
        return locName;
    }
}
