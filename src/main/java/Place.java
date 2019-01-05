import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;

/**
 * Klasse zum Speichern von "Orten", diese können entweder Pickup oder Delivery Locations sein, wird hier nicht weiter unterschieden(evtl. sinnvoll?)
 */
public class Place {
    private double lat;
    private double lon;
    private String city;
    private String street;
    private int houseNo;
    private int zip;
    private int locID;
    private TimeWindow timeWindow;

    /**
     *
     * @param lat latitude des Ortes
     * @param lon longitude des Ortes
     * @param city Name des ortes
     * @param locID ID des Ortes
     * @param timeWindow Zeitfenster zum Abarbeiten des Jobs
     */
    public Place(double lat, double lon, String city, String street, int houseNo, int zip, int locID, TimeWindow timeWindow) {
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.street = street;
        this.houseNo = houseNo;
        this.zip = zip;
        this.locID = locID;
        this.timeWindow = timeWindow;
    }

    public Place(double lat, double lon, String city, String street, int houseNo, int zip, int locID) {
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.street = street;
        this.houseNo = houseNo;
        this.zip = zip;
        this.locID = locID;

    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
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

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public int getHouseNo() {
        return houseNo;
    }

    public int getZip() {
        return zip;
    }

    public int getLocID() {
        return locID;
    }

    public String toString() {
        return city;
    }
}
