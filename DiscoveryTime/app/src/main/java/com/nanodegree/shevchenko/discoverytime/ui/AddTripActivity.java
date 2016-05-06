package com.nanodegree.shevchenko.discoverytime.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private static final String LOG_TAG = AddTripActivity.class.getName();
    private Trip mTrip = new Trip();

    @BindView(R.id.title) EditText mTitle;
    @BindView(R.id.start_date) EditText mStartDate;
    @BindView(R.id.end_date) EditText mEndDate;
    @BindView(R.id.place_autocomplete_fragment) PlaceAutocompleteFragment mAutocomplete;

    private boolean start;
    private long startDate;
    private long endDate;
    private String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        ButterKnife.bind(this);

        // TODO Looks like it is not working now
        View mClearDestination = findViewById(com.google.android.gms.R.id.place_autocomplete_clear_button);
        // Hide clear button in autocomplete field, destination field is required
        mClearDestination.setVisibility(View.GONE);

        mAutocomplete.setHint(getResources().getString(R.string.destination));
        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mTrip.setPlaceId(place.getId());
                placeName = place.getName().toString();
                if (mTitle.getText().length() <= 0) mTitle.setText(placeName);
            }

            @Override
            public void onError(Status status) {
                Log.d(LOG_TAG, "An error occurred while searching for a place: " + status);
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
        // Saving dates to local variables and assign to mTrip after validations
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

        // Save new trip to DB
        String title = mTitle.getText().toString();
        mTrip.setTitle((title.isEmpty()) ? placeName : title);
        mTrip.setStartDate(startDate);
        mTrip.setEndDate(endDate);
        mTrip.save();

        // Close Activity and get back to list
        finish();
    }
}
