package com.nanodegree.shevchenko.discoverytime.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.Calendar;

public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String LOG_TAG = AddTripActivity.class.getName();
    private Trip mTrip = new Trip();

    private PlaceAutocompleteFragment mAutocomplete;
    private EditText mTitle;
    private EditText mStartDate;
    private EditText mEndDate;
    private Button mSave;

    private boolean start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        mAutocomplete = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        mTitle = (EditText) findViewById(R.id.title);
        mStartDate = (EditText) findViewById(R.id.start_date);
        mEndDate = (EditText) findViewById(R.id.end_date);

        mAutocomplete.setHint(getResources().getString(R.string.destination));
        // TODO if place was deleted and new was not selected
        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mTrip.setId(place.getId());
                String placeName = place.getName().toString();
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
        String dateString = getResources().getString(R.string.date_template,
                dayOfMonth, monthOfYear + 1, year);
        if (start) {
            mStartDate.setText(dateString);
            // TODO start and end date should be in date format, not strings like now
            //mTrip.setDateStart();
        } else {
            mEndDate.setText(dateString);
            //mTrip.setDateEnd();
        }
    }
}
