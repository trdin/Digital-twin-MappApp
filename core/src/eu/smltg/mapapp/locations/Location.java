package eu.smltg.mapapp.locations;

public class Location {
    String type;
    Double[] coordinates;

    @Override
    public String toString() {
        return " [" + this.coordinates[0] + " : " + this.coordinates[1] + "]";
    }
}
