package eu.smltg.mapapp.locations;

import com.google.gson.Gson;

import java.io.IOException;

import eu.smltg.mapapp.Const;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class People {

    public Location location;
    public String number;
    public String time;

    public static People[] getPeopleAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.apiLink + "images") // Saved in images model
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.body() != null) {
            String responseString = response.body().string();
            Gson gson = new Gson();

            return gson.fromJson(responseString, People[].class);
        }else {
            return new People[]{};
        }
    }

}
