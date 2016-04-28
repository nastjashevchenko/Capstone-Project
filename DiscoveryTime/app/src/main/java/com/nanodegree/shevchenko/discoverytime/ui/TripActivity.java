package com.nanodegree.shevchenko.discoverytime.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

public class TripActivity extends AppCompatActivity {

    private Trip mTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTrip = getIntent().getParcelableExtra(Trip.EXTRA_NAME);
        TextView titleView = (TextView) findViewById(R.id.title);
        TextView datesView = (TextView) findViewById(R.id.trip_dates);
        titleView.setText(mTrip.getTitle());
        datesView.setText(mTrip.getDates());
    }

}
