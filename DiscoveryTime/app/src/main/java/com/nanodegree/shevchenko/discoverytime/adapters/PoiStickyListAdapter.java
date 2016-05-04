package com.nanodegree.shevchenko.discoverytime.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.model.Poi;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class PoiStickyListAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private List<Poi> poiList;
    private Context context;

    public PoiStickyListAdapter(Context context, List<Poi> poiList) {
        this.context = context;
        this.poiList = poiList;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        TextView titleView = new TextView(context);
        Poi poi = poiList.get(position);
        String day = (poi.getDay() == -1) ? "Not planned yet" : poi.getDay().toString();
        titleView.setText(day);
        titleView.setTextSize(24);
        titleView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

        convertView = titleView;
        return  convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return poiList.get(position).getDay();
    }

    @Override
    public int getCount() {
        return poiList.size();
    }

    @Override
    public Object getItem(int position) {
        return poiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView titleView = new TextView(context);
        Poi poi = poiList.get(position);
        titleView.setText(poi.getName());
        titleView.setTextSize(18);

        convertView = titleView;

        return convertView;
    }
}
