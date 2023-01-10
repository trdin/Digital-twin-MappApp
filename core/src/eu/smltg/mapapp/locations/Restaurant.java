package eu.smltg.mapapp.locations;

import com.badlogic.gdx.utils.Logger;
import com.google.gson.Gson;

import java.io.IOException;

import eu.smltg.mapapp.Const;
import eu.smltg.mapapp.DataVisualiserMap;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Restaurant {
    public static boolean locationFilter = true;

    public Location location;
    public String _id;
    public String name;
    public String address;

    private static final Logger log = new Logger(DataVisualiserMap.class.getSimpleName(), Logger.DEBUG);


    /*"_id": "6278fb184365cb3b54730bd6",
    "name": "Zlati Lev",
    "address": "Lo�ka ulica 10, Maribor",
    "dataSeries": "62753385bc8191993bdd939f",
    "__v": 0*/


    @Override
    public String toString() {
        return this.name + " + " + this.location.toString() + "i";
    }

    public static Restaurant[] getRestaurantsAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.apiLink + "restaurants")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.body() != null){
            String responseRes = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(responseRes, Restaurant[].class);
        }else{
            return new Restaurant[]{};
        }
    }
}
