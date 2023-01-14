package eu.smltg.mapapp.locations;

import com.badlogic.gdx.utils.Logger;
import com.google.gson.Gson;

import java.io.IOException;

import eu.smltg.mapapp.Const;
import eu.smltg.mapapp.DataVisualiserMap;
import eu.smltg.mapapp.utils.PixelPosition;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Wifi {
    public static boolean locationFilter = true;

    public Location location;
    public String _id;
    public String wifiId;
    public String name;
    public String address;

    public PixelPosition pixelPos;

    private static final Logger log = new Logger(DataVisualiserMap.class.getSimpleName(), Logger.DEBUG);

    public static Wifi[] getWifiAPI() throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.apiLink+ "wifi")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.body() != null && response.code() == 200) {
            String responseRes = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(responseRes, Wifi[].class);
        }else{
            return new Wifi[]{};
        }
    }
//    "location": {
//        "type": "Point",
//                "coordinates": [46.5594185, 15.6451287]
//    },
//            "_id": "63b554779d1d9c72665bef7c",
//            "name": "University of Maribor ",
//            "password": "",
//            "dataSeries": "63b5545d9d1d9c72665bef75",
//            "wifiId": "81348424",
//            "__v": 0
}
