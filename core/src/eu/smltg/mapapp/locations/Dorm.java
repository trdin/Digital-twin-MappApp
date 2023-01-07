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

public class Dorm {

    public String place_id;
    public String lat;
    public String lon;
    public String display_name;
    public int place_rank;
    public GeoJson geojson;

    public float[] getPolygon(int tileSize, int zoom, int beginTileX, int beginTileY, int height){
        float[] polygon = new float[geojson.coordinates[0].length * 2];

        int i = 0;

        for(float[] geojson: geojson.coordinates[0]) {
            PixelPosition point = MapRasterTiles.getPixelPosition(geojson[1], geojson[0], tileSize, zoom, beginTileX, beginTileY, height);
            polygon[i] = point.x;
            i++;
            polygon[i] = point.y;
            i++;
        }
        return polygon;
    }

    public static Dorm[] getDormAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.nominatimApiLink + "search.php?q=študentski+dom+Maribor&format=jsonv2&polygon_geojson=1")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.body() != null) {
            String responseString = response.body().string();
            Gson gson = new Gson();

            return gson.fromJson(responseString, Dorm[].class);
        }else {
            return new Dorm[]{};
        }
    }
}
