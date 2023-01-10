package eu.smltg.mapapp.locations;

public class Location {
    String type;
    public double[] coordinates;

    public Location(double lat, double lon) {
        coordinates = new double[]{lat,lon};
    }

    public double getLat() {
        return coordinates[0];
    }
    public double getLon() {
        return coordinates[1];
    }

    @Override
    public String toString() {
        return " [" + this.coordinates[0] + " : " + this.coordinates[1] + "]";
    }
}
