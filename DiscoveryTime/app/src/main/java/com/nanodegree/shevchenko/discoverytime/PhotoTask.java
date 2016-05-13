package com.nanodegree.shevchenko.discoverytime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private GoogleApiClient mGoogleApiClient;

    public PhotoTask(Context c, GoogleApiClient apiClient, String placeId) {
        mContext = c;
        mGoogleApiClient = apiClient;
        mPlaceId = placeId;
    }

    private void saveImage(Bitmap image, File file) {
        if (image == null) return;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String coordFloor(double coord) {
        // i.e. London is 51.5074° N, 0.1278° W
        // want to look at bounds +- 0.1: [51.4, 51.6]
        return String.valueOf(Math.floor(coord * 10 - 1) / 10);
    }

    private String coordCeil(double coord) {
        // i.e. London is 51.5074° N, 0.1278° W
        // want to look at bounds +- 0.1: [51.4, 51.6]
        return String.valueOf(Math.floor(coord * 10 + 1) / 10);
    }

    @Override
    protected Bitmap doInBackground(Double... params) {
        if (params.length != 2) {
            return null;
        }
        Bitmap image = null;
        // TODO Think of image sizes (OOM)
        // Check if file exists locally and use it
        // If not - download from web and save for future use
        File file  = new File (mContext.getFilesDir(), mPlaceId);
        Log.d(LOG_TAG, file.toString());
        if (file.exists()) try {
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, mPlaceId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                // TODO width, height
                image  = photo.getScaledPhoto(mGoogleApiClient, 300, 300).await()
                        .getBitmap();
            }
            photoMetadataBuffer.release();
        }

        if (image == null) {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("www.panoramio.com")
                    .addPathSegments("map/get_panoramas.php")
                    .addQueryParameter("set", "places")
                    .addQueryParameter("from", "0")
                    .addQueryParameter("to", "20")
                    .addQueryParameter("size", "medium")
                    // TODO search boundaries should depend on place size (i.e. country, city)
                    .addQueryParameter("minx", coordFloor(params[1]))
                    .addQueryParameter("miny", coordFloor(params[0]))
                    .addQueryParameter("maxx", coordCeil(params[1]))
                    .addQueryParameter("maxy", coordCeil(params[0]))
                    .build();

            Log.d("URL ", url.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String photoUrl = new JSONObject(response.body().string())
                        .getJSONArray("photos")
                        .getJSONObject(0)
                        .getString("photo_file_url");
                image = BitmapFactory.decodeStream(new URL(photoUrl).openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveImage(image, file);
        return image;
    }
}