package eu.smltg.mapapp.locations;

import com.google.gson.Gson;

import java.io.IOException;

import eu.smltg.mapapp.Const;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Bar {

    public String place_id;
    public String name;
    public String address;
    public Location location;


    public static Bar[] getBarsAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.apiLink + "bars")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.body() != null) {
            String responseString = response.body().string();
            Gson gson = new Gson();

            return gson.fromJson(responseString, Bar[].class);
        }else {
            return new Bar[]{};
        }
    }
}