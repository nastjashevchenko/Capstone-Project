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
import com.nanodegree.shevchenko.discoverytime.Util;
import com.nanodegree.shevchenko.discoverytime.model.Trip;
import com.nanodegree.shevchenko.discoverytime.model.TripPlace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        EditPlaceDialog.EditPlaceDialogListener {

    private List<TripPlace> mTripPlaces;
    private Map<Marker, TripPlace> mMarkerPlaceMap;
    private long startDate;
    private long endDate;

    private static final float[] MARKER_COLORS = new float[] {330.0F, 0.0F, 30.0F,
            60.0F, 90.0F, 120.0F, 150.0F, 180.0F, 210.0F, 240.0F, 270.0F, 300.0F};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mTripPlaces = getIntent().getParcelableArrayListExtra(TripPlace.PLACES_LIST);
        startDate = getIntent().getLongExtra(Trip.START_DATE, 0L);
        endDate = getIntent().getLongExtra(Trip.END_DATE, 0L);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void addMarkerForPlace(GoogleMap googleMap, LatLngBounds.Builder builder, TripPlace tripPlace) {
        LatLng placeLatLng = new LatLng(tripPlace.getLat(), tripPlace.getLng());
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(placeLatLng)
                .icon(getMarkerIcon(tripPlace))
                .title(tripPlace.getName())
                .snippet(getSnippet(tripPlace)));
        builder.include(placeLatLng);
        mMarkerPlaceMap.put(marker, tripPlace);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnInfoWindowClickListener(this);
        if (mMarkerPlaceMap == null) mMarkerPlaceMap = new HashMap<>();
        int padding = 100;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (TripPlace tripPlace : mTripPlaces) {
            addMarkerForPlace(googleMap, builder, tripPlace);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        DialogFragment dialog = EditPlaceDialog.newInstance(mMarkerPlaceMap.get(marker),
                startDate, endDate);
        dialog.show(getSupportFragmentManager(), "EditPlaceDialog");
    }

    private Marker findMarkerByPlace(TripPlace place) {
        for (Map.Entry<Marker, TripPlace> entry : mMarkerPlaceMap.entrySet()) {
            if (entry.getValue().equals(place)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getSnippet(TripPlace tripPlace) {
        Integer day = tripPlace.getDay();
        if (day == 0) {
            return getString(R.string.not_planned_header);
        } else {
            return getString(R.string.day_and_date_tmpl, day, Util.getDateByDayNumber(startDate, day));
        }
    }

    private BitmapDescriptor getMarkerIcon(TripPlace tripPlace) {
        Integer day = tripPlace.getDay();
        if (day == 0) {
            return BitmapDescriptorFactory.fromResource(R.drawable.grey_marker);
        } else {
            // There are 12 pre defined colors, if number of days is greater - just start over
            return BitmapDescriptorFactory.defaultMarker(MARKER_COLORS[day % 12]);
        }
    }

    @Override
    public void onSaveClick(DialogFragment dialog, TripPlace place) {
        Marker marker = findMarkerByPlace(place);
        if (marker != null) {
            marker.setSnippet(getSnippet(place));
            marker.setIcon(getMarkerIcon(place));
        }
    }
}
