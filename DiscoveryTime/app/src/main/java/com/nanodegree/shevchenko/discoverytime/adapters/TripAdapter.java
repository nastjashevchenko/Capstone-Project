package com.nanodegree.shevchenko.discoverytime.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.List;

public class TripAdapter extends BaseAdapter {
    private List<Trip> tripList;
    private final Context context;

    public TripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @Override
    public int getCount() {
        return tripList.size();
    }

    @Override
    public Trip getItem(int position) {
        return tripList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.trip_item, parent, false);
        }
        TextView titleView = (TextView) convertView.findViewById(R.id.trip_title);
        TextView datesView = (TextView) convertView.findViewById(R.id.trip_dates);

        Trip trip = tripList.get(position);

        titleView.setText(trip.getTitle());
        datesView.setText(trip.getDates(context.getResources().getString(R.string.from_to_tmpl)));

        return convertView;
    }
}
