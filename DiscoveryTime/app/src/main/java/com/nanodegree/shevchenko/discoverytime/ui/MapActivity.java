package com.nanodegree.shevchenko.discoverytime.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.model.Poi;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.List;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private Trip mTrip;
    private List<Poi> mPois;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mTrip = Trip.getById(getIntent().getLongExtra(Trip.EXTRA_ID_NAME, 0L));
        mPois = mTrip.getPois();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void addMarkerForPoi(GoogleMap googleMap, LatLngBounds.Builder builder, Poi poi) {
        LatLng poiLatLng = new LatLng(poi.getLat(), poi.getLng());
        googleMap.addMarker(new MarkerOptions()
                .position(poiLatLng)
                .title(poi.getName()));
        builder.include(poiLatLng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        int padding = 100;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Poi poi : mPois) {
            addMarkerForPoi(googleMap, builder, poi);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
    }
}
