package com.shevchenko.discoverytime.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shevchenko.discoverytime.R;
import com.shevchenko.discoverytime.model.Trip;


public class TripCursorAdapter extends CursorAdapter {

    public TripCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.trip_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleView = (TextView) view.findViewById(R.id.trip_title);
        TextView datesView = (TextView) view.findViewById(R.id.trip_dates);

        Trip trip = new Trip(cursor);

        titleView.setText(trip.getTitle());
        datesView.setText(trip.getDates(context.getResources().getString(R.string.from_to_tmpl)));
    }
}