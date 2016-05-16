package com.nanodegree.shevchenko.discoverytime.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.adapters.TripListPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements OnConnectionFailedListener {
    private static final String LOG_TAG = MainActivity.class.getName();

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
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, connectionResult.getErrorMessage());
    }

    public void openAddActivity(View view) {
        DialogFragment dialog = EditTripDialog.newInstance();
        dialog.show(getSupportFragmentManager(), "EditTripDialog");
    }
}
