package com.nanodegree.shevchenko.discoverytime.adapters;

import android.content.Context;
import android.view.LayoutInflater;
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
    private final LayoutInflater inflater;

    public PoiStickyListAdapter(Context context, List<Poi> poiList) {
        this.context = context;
        this.poiList = poiList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_day_header, parent, false);
        }

        Poi poi = poiList.get(position);
        Integer day = poi.getDay();

        String dayStr = (day == 0)
                ? context.getResources().getString(R.string.not_planned_header)
                : context.getResources().getString(R.string.day_number, day);
        TextView dayNumberView = (TextView) convertView.findViewById(R.id.day_number);
        TextView dateView = (TextView) convertView.findViewById(R.id.date);

        dayNumberView.setText(dayStr);
        dateView.setText(poi.getDateStr());

        return convertView;
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
        // TODO create xml files + add note to view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_day_header, parent, false);
        }

        TextView titleView = new TextView(context);
        Poi poi = poiList.get(position);
        titleView.setText(poi.getName());
        titleView.setTextSize(20);
        titleView.setPadding(0, 24, 0, 24);

        convertView = titleView;

        return convertView;
    }
}
