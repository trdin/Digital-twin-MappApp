package eu.smltg.mapapp.locations;

public class Location {
    String type;
    public Double[] coordinates;

    @Override
    public String toString() {
        return " [" + this.coordinates[0] + " : " + this.coordinates[1] + "]";
    }
}
