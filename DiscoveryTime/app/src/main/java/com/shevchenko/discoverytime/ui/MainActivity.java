package com.shevchenko.discoverytime.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.shevchenko.discoverytime.R;
import com.shevchenko.discoverytime.adapters.TripListPagerAdapter;
import com.shevchenko.discoverytime.model.Trip;
import com.shevchenko.discoverytime.model.TripPlace;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements OnConnectionFailedListener {
    private static final String LOG_TAG = MainActivity.class.getName();
    public static final String DELETE_EVENT = "DELETE_EVENT";

    private BroadcastReceiver messageReceiver;

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.sliding_tabs) TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mViewPager.setAdapter(new TripListPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));
        mTabLayout.setupWithViewPager(mViewPager);

        messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(DELETE_EVENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, connectionResult.getErrorMessage());
    }

    public void openAddActivity(View view) {
        DialogFragment dialog = EditTripDialog.newInstance();
        dialog.show(getSupportFragmentManager(), "EditTripDialog");
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DELETE_EVENT)) {
                final Trip trip = intent.getParcelableExtra(Trip.EXTRA_NAME);
                final ArrayList<TripPlace> places = intent.getParcelableArrayListExtra(TripPlace.PLACES_LIST);
                Snackbar deleted = Snackbar.make(
                        findViewById(R.id.coordinator_layout),
                        getString(R.string.trip_deleted, trip.getTitle()),
                        Snackbar.LENGTH_LONG);
                deleted = setTextColor(deleted);
                deleted.setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar restored = Snackbar.make(findViewById(R.id.coordinator_layout),
                                getString(R.string.trip_restored, trip.getTitle()),
                                Snackbar.LENGTH_SHORT);
                        restored = setTextColor(restored);
                        restored.show();
                        trip.insertWithPlaces(getContentResolver(), places);
                    }
                });
                deleted.show();
            }
        }
    }

    private Snackbar setTextColor(Snackbar snackbar) {
        TextView text = (TextView) snackbar.getView()
                .findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(Color.WHITE);
        return snackbar;
    }
}
