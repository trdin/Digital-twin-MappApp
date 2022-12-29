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

//TODO points markers for faculties and than polygons for the actua buildings
public class Faculty {
    public String display_name;
    public String[] boundingbox;
    //center
    public String lat;
    public String lon;

    //start of the shape
    public Integer x;
    public Integer y;


    public static Faculty[] getFacultyAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.nominatimApiLink + "/search?q=[amenity=university]&format=json&bounded=true&viewbox=15.60041,46.56974,15.70186,46.53704&limit=100")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if (response.body() != null) {
            String responseRes = response.body().string();

            Gson gson = new Gson();

            return gson.fromJson(responseRes, Faculty[].class);
        } else {
            return new Faculty[]{};
        }
    }

    public Integer[] shape(int tileSize, int zoom, int beginTileX, int beginTileY, int height){
        Double lat1 = Double.parseDouble(boundingbox[0]);
        Double lon1 = Double.parseDouble(boundingbox[2]);

        PixelPosition point1 = MapRasterTiles.getPixelPosition(lat1, lon1,  tileSize,zoom, beginTileX,beginTileY,height);

        x = point1.x;
        y = point1.y;

        Double lat2 = Double.parseDouble(boundingbox[1]);
        Double lon2 = Double.parseDouble(boundingbox[3]);

        PixelPosition point2 = MapRasterTiles.getPixelPosition(lat2, lon2,  tileSize,zoom, beginTileX,beginTileY,height);

        Integer widthShape = point2.x - point1.x;
        Integer heightShape = point2.y -point1.y;

        return new Integer[]{widthShape, heightShape};

    }
}
