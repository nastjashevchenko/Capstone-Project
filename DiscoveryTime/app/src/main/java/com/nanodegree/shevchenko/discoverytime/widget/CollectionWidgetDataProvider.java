package com.nanodegree.shevchenko.discoverytime.widget;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.data.TripContract;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

public class CollectionWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    Cursor cursor;

    public CollectionWidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                R.layout.collection_widget_item);

        cursor.moveToPosition(position);
        Trip trip = new Trip(cursor);

        mView.setTextViewText(R.id.trip_title, trip.getTitle());
        mView.setTextViewText(R.id.trip_dates, trip.getDates(mContext.getString(R.string.from_to_tmpl)));

        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(Trip.EXTRA_NAME, trip);
        mView.setOnClickFillInIntent(R.id.trip_item, fillInIntent);

        return mView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    private void initData() {
        cursor = mContext.getContentResolver().query(
                TripContract.TripColumns.CONTENT_URI,
                null,
                TripContract.TripColumns.END_DATE + " >= ?",
                new String[]{String.valueOf(System.currentTimeMillis())},
                TripContract.TripColumns.START_DATE + " ASC"
        );
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }
}