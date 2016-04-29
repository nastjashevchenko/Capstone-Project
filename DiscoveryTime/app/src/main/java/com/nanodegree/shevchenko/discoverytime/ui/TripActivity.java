package com.nanodegree.shevchenko.discoverytime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.model.Poi;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripActivity extends AppCompatActivity {

    private Trip mTrip;
    private List<Poi> mPois;
    private List<String> mNames = new ArrayList<>();

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;
    private static final String LOG_TAG = TripActivity.class.getName();

    private ListView mPoiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO Query by id for now. Change when get to final data model
        mTrip = Trip.getById(getIntent().getLongExtra(Trip.EXTRA_ID_NAME, 0L));
        mPoiList = (ListView) findViewById(R.id.poi_list);
        TextView titleView = (TextView) findViewById(R.id.title);
        TextView datesView = (TextView) findViewById(R.id.trip_dates);
        titleView.setText(mTrip.getTitle());
        datesView.setText(mTrip.getDates());

        // TODO Use cursor adapter
        mPois = mTrip.getPois();
        for (Poi p : mPois) {
            mNames.add(p.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mNames);
        mPoiList.setAdapter(adapter);

    }

    public void addPlace(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            // TODO Create places filter, I don't need all types
                            //.setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Poi poi = new Poi(place.getId(), place.getName().toString(), mTrip);
                poi.save();
                Log.i(LOG_TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(LOG_TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
