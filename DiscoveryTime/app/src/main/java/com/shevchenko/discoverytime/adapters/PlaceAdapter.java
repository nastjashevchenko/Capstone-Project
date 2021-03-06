package com.shevchenko.discoverytime.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shevchenko.discoverytime.R;
import com.shevchenko.discoverytime.Util;
import com.shevchenko.discoverytime.model.TripPlace;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    private List<ListItem> mItemList;
    private OnRecyclerItemClickListener mListener;
    private Context mContext;
    private long mStartDate;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PLACE = 1;

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(TripPlace place);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        // For header title is day number or "Not planned yet" text, note is date (can be empty)
        // For place item title is place name, note is note added bu user (can be empty)
        TextView title;
        TextView note;
        OnRecyclerItemClickListener mListener;

        public ViewHolder(View placeItemView, OnRecyclerItemClickListener listener) {
            super(placeItemView);
            title = (TextView) placeItemView.findViewById(R.id.title);
            note = (TextView) placeItemView.findViewById(R.id.note);
            placeItemView.setOnClickListener(this);
            placeItemView.setOnLongClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View view) {
            ListItem item = mItemList.get(getLayoutPosition());
            if (item.isHeader) return;
            mListener.onRecyclerItemClick(item.place);
        }

        @Override
        public boolean onLongClick(View v) {
            final ListItem item = mItemList.get(getLayoutPosition());
            if (item.isHeader) return true;
            final String placeName = item.place.getName();
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setMessage(mContext.getString(R.string.place_delete_confirm, placeName))
                    .setTitle(placeName)
                    .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            item.place.delete(mContext.getContentResolver());
                            Toast.makeText(mContext,
                                    mContext.getString(R.string.place_deleted, placeName),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            alert.create().show();
            return true;
        }
    }

    public PlaceAdapter(Context context, Cursor tripCursor, long startDate) {
        mContext = context;
        mListener = (OnRecyclerItemClickListener) context;
        mStartDate = startDate;
        setUpdatedList(tripCursor);
    }

    public void setUpdatedList(Cursor tripCursor) {
        mItemList = new ArrayList<>();
        // Need to build list of recyclerview items, need to insert headers to list of places
        int lastDayNumber = -1;
        if (tripCursor == null || tripCursor.getCount() <= 0) return;
        // In case we already retrieved data and went through cursor, e.g. on device rotation
        tripCursor.moveToFirst();
        do {
            TripPlace tripPlace = new TripPlace(tripCursor);
            int day = tripPlace.getDay();
            if (day != lastDayNumber) {
                // Insert new header view and update section data.
                lastDayNumber = day;
                String dayStr = (day == 0)
                        ? mContext.getResources().getString(R.string.not_planned_header)
                        : mContext.getResources().getString(R.string.day_number, day);
                mItemList.add(new ListItem(dayStr, Util.getDateByDayNumber(mStartDate, day), true, null));
            }
            mItemList.add(new ListItem(tripPlace.getName(), tripPlace.getNote(), false, tripPlace));
        } while (tripCursor.moveToNext());
    }

    @Override
    public int getItemViewType(int position) {
        ListItem item = mItemList.get(position);
        return item.isHeader ? TYPE_HEADER : TYPE_PLACE;
    }

    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_day_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_place, parent, false);
        }
        return new ViewHolder(view, mListener);
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
        // Save TripPlace object if it is not header
        TripPlace place;

        public ListItem(String title, String note, boolean isHeader, TripPlace place) {
            this.isHeader = isHeader;
            this.title = title;
            this.note = note;
            this.place = place;
        }
    }
}