package com.shevchenko.discoverytime;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoTask extends AsyncTask<Double, Void, Bitmap> {
    private static final String LOG_TAG = PhotoTask.class.getName();
    private Context mContext;
    private String mPlaceId;
    private GoogleApiClient mGoogleApiClient;
    private int mWidth;
    private int mHeight;

    public PhotoTask(Context c, GoogleApiClient apiClient,
                     String placeId, int width, int height) {
        mContext = c;
        mGoogleApiClient = apiClient;
        mPlaceId = placeId;
        mWidth = width;
        mHeight = height;
    }

    private void saveImage(Bitmap image, File file) {
        if (image == null) return;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Bitmap doInBackground(Double... params) {
        if (params.length != 2) {
            return null;
        }
        Bitmap image = null;
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
                image  = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                        .getBitmap();
            }
            photoMetadataBuffer.release();
        }
        saveImage(image, file);
        return image;
    }
}