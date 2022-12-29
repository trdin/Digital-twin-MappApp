package eu.smltg.mapapp.locations;

public class Restaurant {

    Location location;
    String _id;
    String name;
    String address;
    String dataSeries;
    Integer __v;

    /*"_id": "6278fb184365cb3b54730bd6",
    "name": "Zlati Lev",
    "address": "Lo�ka ulica 10, Maribor",
    "dataSeries": "62753385bc8191993bdd939f",
    "__v": 0*/


    @Override
    public String toString() {
        return this.name + " + " + this.location.toString() + "i";
    }

}
