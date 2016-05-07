package com.nanodegree.shevchenko.discoverytime.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.model.Poi;

import java.util.ArrayList;
import java.util.List;

public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.ViewHolder> {
    private List<ListItem> mItemList = new ArrayList<>();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_POI = 1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // For header title is day number or "Not planned yet" text, note is date (can be empty)
        // For poi item title is place name, note is note added bu user (can be empty)
        TextView title;
        TextView note;

        public ViewHolder(View poiItemView) {
            super(poiItemView);
            title = (TextView) poiItemView.findViewById(R.id.title);
            note = (TextView) poiItemView.findViewById(R.id.note);
        }
    }

    public PoiAdapter(Context context, List<Poi> poiList) {
        // Need to build list of recyclerview items, need to insert headers to list of places
        int lastDayNumber = -1;
        for (int i = 0; i < poiList.size(); i++) {
            Poi poi = poiList.get(i);
            int dayNumber = poi.getDay();
            if (dayNumber != lastDayNumber) {
                // Insert new header view and update section data.
                lastDayNumber = dayNumber;
                String dayStr = (dayNumber == 0)
                        ? context.getResources().getString(R.string.not_planned_header)
                        : context.getResources().getString(R.string.day_number, dayNumber);
                mItemList.add(new ListItem(dayStr, poi.getDateStr(), true));
            }
            mItemList.add(new ListItem(poi.getName(), poi.getNote(), false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        final ListItem item = mItemList.get(position);
        return item.isHeader ? TYPE_HEADER : TYPE_POI;
    }

    @Override
    public PoiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_day_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_poi, parent, false);
        }
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String note = mItemList.get(position).note;
        holder.title.setText(mItemList.get(position).title);
        holder.note.setText((note != null && note.length() > 0) ? note : "");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    // Class to hold all items of recycler view: both days headers and places
    private static class ListItem {
        boolean isHeader;
        String title;
        String note;

        public ListItem(String title, String note, boolean isHeader) {
            this.isHeader = isHeader;
            this.title = title;
            this.note = note;
        }
    }

}