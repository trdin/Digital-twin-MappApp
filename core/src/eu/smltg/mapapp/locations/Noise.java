package eu.smltg.mapapp.locations;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import eu.smltg.mapapp.Const;
import eu.smltg.mapapp.utils.PixelPosition;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Noise {

    public static boolean locationFilter = false;
    public PixelPosition pixelPos;

    public Double lat;
    public Double lon;
    public Double noise;
    public String time; // Used for conversion from string to Date
    public Date dateTime;

    public static Noise[] getNoiseBlockchainAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.blockchainApiLink + "noise")
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.body() != null && response.code() == 200){
            String responseString = response.body().string();
            Gson gson = new Gson();

            return gson.fromJson(responseString, Noise[].class);
        }else {
            return new Noise[]{};
        }
    }
}
