package com.nanodegree.shevchenko.discoverytime.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.adapters.PoiStickyListAdapter;
import com.nanodegree.shevchenko.discoverytime.model.Poi;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TripActivity extends AppCompatActivity {

    private Trip mTrip;
    private List<Poi> mPois;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;
    private static final String LOG_TAG = TripActivity.class.getName();

    private StickyListHeadersListView mPoiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO Query by id for now. Change when get to final data model
        mTrip = Trip.getById(getIntent().getLongExtra(Trip.EXTRA_ID_NAME, 0L));
        mPoiList = (StickyListHeadersListView) findViewById(R.id.poi_list);
        TextView titleView = (TextView) findViewById(R.id.title);
        TextView datesView = (TextView) findViewById(R.id.trip_dates);
        titleView.setText(mTrip.getTitle());
        datesView.setText(mTrip.getDates());

        // TODO Use cursor adapter
        mPois = mTrip.getPois();
        PoiStickyListAdapter adapter = new PoiStickyListAdapter(this, mPois);
        mPoiList.setAdapter(adapter);

        mPoiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_poi_edit, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(dialogView);
                final Spinner spinner = (Spinner) dialogView.findViewById(R.id.days_spinner);
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter(view.getContext(),
                        android.R.layout.simple_spinner_item, mTrip.getAllDates());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(mPois.get(position).getDay() + 1);
                final EditText note = (EditText) dialogView.findViewById(R.id.note);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // TODO recreate list after day is changed
                                        // because list is sorted by day
                                        Poi poi = mPois.get(position);
                                        poi.setDay(spinner.getSelectedItemPosition() - 1);
                                        poi.setNote(note.getText().toString());
                                        poi.save();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // TODO Add delete, edit and share functions
        if (id == R.id.action_add_place) {
            addPlace();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addPlace() {
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
