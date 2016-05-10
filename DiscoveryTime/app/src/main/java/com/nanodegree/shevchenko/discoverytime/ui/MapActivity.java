package com.nanodegree.shevchenko.discoverytime.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.model.Poi;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        EditPoiDialog.EditPoiDialogListener {

    private Trip mTrip;
    private List<Poi> mPois;
    private Map<Marker, Long> mMarkerPoiMap;

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
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(poiLatLng)
                .icon(getMarkerIcon(poi))
                .title(poi.getName())
                .snippet(getSnippet(poi)));
        builder.include(poiLatLng);
        mMarkerPoiMap.put(marker, poi.getId());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnInfoWindowClickListener(this);
        if (mMarkerPoiMap == null) mMarkerPoiMap = new HashMap<>();
        int padding = 100;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Poi poi : mPois) {
            addMarkerForPoi(googleMap, builder, poi);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        DialogFragment dialog = EditPoiDialog.newInstance(mMarkerPoiMap.get(marker));
        dialog.show(getSupportFragmentManager(), "EditPoiDialog");
    }

    private Marker findMarkerByPoiId(Long poiId) {
        for (Map.Entry<Marker, Long> entry : mMarkerPoiMap.entrySet()) {
            if (entry.getValue().equals(poiId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getSnippet(Poi poi) {
        Integer day = poi.getDay();
        if (day == 0) {
            return getString(R.string.not_planned_header);
        } else {
            return getString(R.string.day_and_date_tmpl, day, poi.getDateStr());
        }
    }

    private BitmapDescriptor getMarkerIcon(Poi poi) {
        Integer day = poi.getDay();
        if (day == 0) {
            return BitmapDescriptorFactory.fromResource(R.drawable.grey_marker);
        } else {
            // There are 10 pre defined colors, if number of days is greater - just start over
            // TODO add more colors
            return BitmapDescriptorFactory.defaultMarker(MARKER_COLORS[day % 10]);
        }
    }

    @Override
    public void onSaveClick(DialogFragment dialog, Long poiId) {
        Marker marker = findMarkerByPoiId(poiId);
        Poi poi = Poi.getById(poiId);
        if (marker != null) {
            marker.setSnippet(getSnippet(poi));
            marker.setIcon(getMarkerIcon(poi));
        }
    }
}
