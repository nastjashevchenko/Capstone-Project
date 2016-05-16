package com.nanodegree.shevchenko.discoverytime.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.ui.TripActivity;

public class CollectionWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews mView = new RemoteViews(context.getPackageName(),
                    R.layout.collection_widget_layout);

            Intent intent = new Intent(context, CollectionWidgetService.class);
            mView.setRemoteAdapter(R.id.widget_collection_list, intent);
            mView.setEmptyView(R.id.widget_collection_list, android.R.id.empty);

            Intent clickIntentTemplate = new Intent(context, TripActivity.class);
            PendingIntent clickPendingIntentTemplate = PendingIntent.getActivity(context, 0,
                    clickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
            mView.setPendingIntentTemplate(R.id.widget_collection_list, clickPendingIntentTemplate);

            appWidgetManager.updateAppWidget(widgetId, mView);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
