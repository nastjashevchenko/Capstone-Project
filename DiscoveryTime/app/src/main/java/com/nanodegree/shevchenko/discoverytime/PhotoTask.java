package com.nanodegree.shevchenko.discoverytime;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.URL;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhotoTask extends AsyncTask<Double, Void, Bitmap> {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected Bitmap doInBackground(Double... params) {
        if (params.length != 2) {
            return null;
        }
        // TODO Save bitmap to local storage for future use
        // TODO Think of image sizes (OOM)
        String url = "http://www.panoramio.com/map/get_panoramas.php?set=places&from=1&to=2&size=medium&";
        //&minx=-180&miny=-90&maxx=180&maxy=90
        url = url.concat(String.format(Locale.US,
                "minx=%d&miny=%d&maxx=%d&maxy=%d",
                (int) Math.floor(params[1]),
                (int) Math.floor(params[0]),
                (int) Math.ceil(params[1]),
                (int) Math.ceil(params[0])));

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            String photoUrl = new JSONObject(response.body().string())
                    .getJSONArray("photos")
                    .getJSONObject(0)
                    .getString("photo_file_url");
            return BitmapFactory.decodeStream(new URL(photoUrl).openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}