public class Place {
    private double lat;
    private double lon;
    private String locName;

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
