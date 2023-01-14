package eu.smltg.mapapp.locations;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.tools.javac.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import eu.smltg.mapapp.Const;
import eu.smltg.mapapp.utils.MapRasterTiles;
import eu.smltg.mapapp.utils.PixelPosition;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sun.security.util.ArrayUtil;

//TODO points markers for faculties and than polygons for the actua buildings
public class Faculty {
    public static boolean locationFilter = true;

    public String display_name;

    //center
    public double lat;
    public double lon;

    public FacultyType facultyType;

    public GeoJson geojson;

    public PixelPosition pixelPos;

    public Faculty(String display_name, double lat, double lon) {
        this.display_name = display_name;
        this.lat = lat;
        this.lon = lon;

        facultyType = FacultyType.POINT;
    }

    public Faculty(String display_name, double lat, double lon, GeoJson geojson) {
        this.display_name = display_name;
        this.lat = lat;
        this.lon = lon;
        this.geojson = geojson;

        facultyType = FacultyType.POLYGON;
    }

    public static Faculty[] getFacultyAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.nominatimApiLink + "search?q=[amenity=university]&format=json&bounded=true&viewbox=15.60041,46.56974,15.70186,46.53704&limit=100&polygon_geojson=1")
                .build();
        Gson gson = new Gson();

        Call call = client.newCall(request);
        Response response = call.execute();

        if (response.body() != null && response.code() == 200) {
            String responseRes = response.body().string();
            JSONArray jsonArray = new JSONArray(responseRes);

            ArrayList<Faculty> faculties = new ArrayList<>();
            String display_name;
            double lat;
            double lon;
            FacultyType facultyType;
            GeoJson geojson;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currFacutly = jsonArray.getJSONObject(i);
                display_name = currFacutly.getString("display_name");
                lat = Double.parseDouble(currFacutly.getString("lat"));
                lon = Double.parseDouble(currFacutly.getString("lon"));
                if (currFacutly.getJSONObject("geojson").getString("type").equals("Polygon")) {
                    geojson = gson.fromJson(currFacutly.getJSONObject("geojson").toString(), GeoJson.class);
                    faculties.add(new Faculty(display_name, lat, lon, geojson));
                }
                faculties.add(new Faculty(display_name, lat, lon));
            }
            Faculty[] facultiesArr = new Faculty[faculties.size()];
            return faculties.toArray(facultiesArr);
        } else {
            return new Faculty[]{};
        }
    }

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

//    public Integer[] shape(int tileSize, int zoom, int beginTileX, int beginTileY, int height){
//        double lat1 = Double.parseDouble(boundingbox[0]);
//        double lon1 = Double.parseDouble(boundingbox[2]);
//
//        PixelPosition point1 = MapRasterTiles.getPixelPosition(lat1, lon1,  tileSize,zoom, beginTileX,beginTileY,height);
//
//        x = point1.x;
//        y = point1.y;
//
//        double lat2 = Double.parseDouble(boundingbox[1]);
//        Double lon2 = Double.parseDouble(boundingbox[3]);
//
//        PixelPosition point2 = MapRasterTiles.getPixelPosition(lat2, lon2,  tileSize,zoom, beginTileX,beginTileY,height);
//
//        Integer widthShape = point2.x - point1.x;
//        Integer heightShape = point2.y -point1.y;
//
//        return new Integer[]{widthShape, heightShape};
//
//    }
}
