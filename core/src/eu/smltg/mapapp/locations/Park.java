package eu.smltg.mapapp.locations;

import com.google.gson.Gson;

import java.io.IOException;

import eu.smltg.mapapp.Const;
import eu.smltg.mapapp.utils.MapRasterTiles;
import eu.smltg.mapapp.utils.PixelPosition;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Park {

    public String display_name;
    public String[] boundingbox;
    //center
    public String lat;
    public String lon;

    public GeoJson geojson;


    public float[] getPolygon(int tileSize, int zoom, int beginTileX, int beginTileY, int height) {
        float[] polygon = new float[geojson.coordinates[0].length * 2];


        int i = 0;

        for (float[] go : geojson.coordinates[0]) {
            PixelPosition point = MapRasterTiles.getPixelPosition(go[1], go[0],  tileSize,zoom, beginTileX,beginTileY,height);
            polygon[i] = point.x;
            i++;
            polygon[i] = point.y;
            i++;
        }
        return polygon;
    }

    public static Park[] getParkAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.nominatimApiLink + "search?q=[leisure=park]&format=json&bounded=true&viewbox=15.60041,46.56974,15.70186,46.53704&limit=100&polygon_geojson=1")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if (response.body() != null) {
            String responseRes = response.body().string();

            Gson gson = new Gson();

            return gson.fromJson(responseRes, Park[].class);
        } else {
            return new Park[]{};
        }
    }
}
