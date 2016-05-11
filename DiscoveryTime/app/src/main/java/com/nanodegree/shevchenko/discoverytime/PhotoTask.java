package com.nanodegree.shevchenko.discoverytime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhotoTask extends AsyncTask<Double, Void, Bitmap> {
    private static final String LOG_TAG = PhotoTask.class.getName();
    private final OkHttpClient client = new OkHttpClient();
    private Context mContext;
    private String mPlaceId;
    public PhotoTask(Context c, String placeId) {
        mContext = c;
        mPlaceId = placeId;
    }

    @Override
    protected Bitmap doInBackground(Double... params) {
        if (params.length != 2) {
            return null;
        }
        // TODO Think of image sizes (OOM)

        // Check if file exists locally and use it
        // If not - download from web and save for future use
        File file  = new File (mContext.getFilesDir(), mPlaceId);
        Log.d(LOG_TAG, file.toString());
        if (file.exists()) {
            try {
                return BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("www.panoramio.com")
                    .addPathSegments("map/get_panoramas.php")
                    .addQueryParameter("set", "places")
                    .addQueryParameter("from", "0")
                    .addQueryParameter("to", "1")
                    .addQueryParameter("size", "medium")
                    .addQueryParameter("minx", String.valueOf(Math.floor(params[1]) - 1))
                    .addQueryParameter("miny", String.valueOf(Math.floor(params[0]) - 1))
                    .addQueryParameter("maxx", String.valueOf(Math.ceil(params[1]) + 1))
                    .addQueryParameter("maxy", String.valueOf(Math.ceil(params[0]) + 1))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String photoUrl = new JSONObject(response.body().string())
                        .getJSONArray("photos")
                        .getJSONObject(0)
                        .getString("photo_file_url");
                Bitmap bm = BitmapFactory.decodeStream(new URL(photoUrl).openConnection().getInputStream());
                FileOutputStream fos = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                return bm;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}