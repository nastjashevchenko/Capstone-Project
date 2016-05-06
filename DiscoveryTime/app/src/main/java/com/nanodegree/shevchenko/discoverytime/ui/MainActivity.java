package com.nanodegree.shevchenko.discoverytime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.adapters.TripListPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements OnConnectionFailedListener {
    private static final String LOG_TAG = MainActivity.class.getName();

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.sliding_tabs) TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /* GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        Test sample to test Google Places API connection
        String placeId = "ChIJ-6BSGSeG3EMRllG2gblbMBU";
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            Log.i(LOG_TAG, "Place found: " + myPlace.getName());
                        } else {
                            Log.e(LOG_TAG, "Place not found");
                        }
                        places.release();
                    }
                }); */

        mViewPager.setAdapter(new TripListPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, connectionResult.getErrorMessage());
    }

    public void openAddActivity(View view) {
        Intent addActivity = new Intent(this, AddTripActivity.class);
        startActivity(addActivity);
    }
}
