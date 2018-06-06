/**
 * Klasse zum Speichern von "Orten", diese können entweder Pickup oder Delivery Locations sein, wird hier nicht weiter unterschieden(evtl. sinnvoll?)
 */
public class Place {
    private double lat;
    private double lon;
    private String city;
    private int locID;

    /**
     *
     * @param lat latitude des Ortes
     * @param lon longitude des Ortes
     * @param city Name des ortes
     * @param locID ID des Ortes
     */
    public Place(double lat, double lon, String city, int locID) {
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.locID = locID;
        //TODO (060618, MFO) add street. houseNo, zip
        //+ street
        //+ houseNo
        //+ zip
    }

    @Override
    public boolean equals(Object obj) {
        return this.getLon() == ((Place) obj).getLon() && this.getLat() == ((Place) obj).getLat();
    }

    @Override
    public int hashCode() {
        return (int) (getLat()*10000+getLon()*10000);
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

    public String getCity() {
        return city;
    }

    public String toString() {
        return city;
    }
}
