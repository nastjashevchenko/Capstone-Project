package com.nanodegree.shevchenko.discoverytime.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private static final float[] MARKER_COLORS = new float[] {0.0F, 30.0F,
            60.0F, 120.0F, 180.0F, 210.0F, 240.0F, 270.0F, 300.0F, 330.0F};

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
        Integer day = poi.getDay();
        BitmapDescriptor marker;
        String snippet;
        if (day == 0) {
            marker = BitmapDescriptorFactory.fromResource(R.drawable.grey_marker);
            snippet = getString(R.string.not_planned_header);
        } else {
            // There are 10 pre defined colors, if number of days is greater - just start over
            // TODO add more colors
            marker = BitmapDescriptorFactory.defaultMarker(MARKER_COLORS[day % 10]);
            snippet = getString(R.string.day_and_date_tmpl, day, poi.getDateStr());
        }
        googleMap.addMarker(new MarkerOptions()
                .position(poiLatLng)
                .icon(marker)
                .title(poi.getName())
                .snippet(snippet));
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
