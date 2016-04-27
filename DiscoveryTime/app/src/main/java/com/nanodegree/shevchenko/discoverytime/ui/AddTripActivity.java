package com.nanodegree.shevchenko.discoverytime.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.Util;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.Calendar;

public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private static final String LOG_TAG = AddTripActivity.class.getName();
    private Trip mTrip = new Trip();

    private EditText mTitle;
    private EditText mStartDate;
    private EditText mEndDate;

    private boolean start;
    private long startDate;
    private long endDate;
    private String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        PlaceAutocompleteFragment mAutocomplete = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        mTitle = (EditText) findViewById(R.id.title);
        mStartDate = (EditText) findViewById(R.id.start_date);
        mEndDate = (EditText) findViewById(R.id.end_date);

        mAutocomplete.setHint(getResources().getString(R.string.destination));

        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mTrip.setId(place.getId());
                placeName = place.getName().toString();
                mTrip.setDefaultTitle(placeName);
                if (mTitle.getText().length() <= 0) mTitle.setText(placeName);
            }

            @Override
            public void onError(Status status) {
                Log.d(LOG_TAG, "An error occurred while searching for a place: " + status);
            }
        });

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mTrip.setTitle(s.toString());
            }
        });
    }

    public void showDatePickerDialog(View view) {
        start = (view.getId() == R.id.start_date);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(view.getContext(), this, year, month, day);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        long date = c.getTimeInMillis();
        if (start) {
            startDate = date;
            mStartDate.setText(Util.longDateToString(date));
        } else {
            endDate = date;
            mEndDate.setText(Util.longDateToString(date));
        }
    }

    public void exit(View view) {
        finish();
    }

    private void showError(String error) {
        Toast toast = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void saveAndExit(View view) {
        // Validate destination is set
        if (placeName == null || placeName.isEmpty()) {
            showError(getResources().getString(R.string.destination_empty));
            return;
        }

        // Validate dates
        if ((startDate == 0L && endDate > 0L) || (startDate > 0L && endDate == 0L)) {
            showError(getResources().getString(R.string.one_date_set));
            return;
        }

        if (startDate != 0L && endDate != 0L && startDate > endDate) {
            showError(getResources().getString(R.string.start_after_end));
            return;
        }

        // All fields should be set in Trip, save new trip to DB
        Log.d(LOG_TAG, "Need to save trip here");

        // Close Activity and get back to list
        finish();
    }
}
