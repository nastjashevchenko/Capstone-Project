package com.nanodegree.shevchenko.discoverytime.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.adapters.PoiAdapter;
import com.nanodegree.shevchenko.discoverytime.model.Poi;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripActivity extends AppCompatActivity
        implements PoiAdapter.OnRecyclerItemClickListener, EditPoiDialog.EditPoiDialogListener{

    private Trip mTrip;
    private List<Poi> mPois;
    private RecyclerView.Adapter mAdapter;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;
    private static final String LOG_TAG = TripActivity.class.getName();

    @BindView(R.id.poi_list) RecyclerView mPoiListView;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.trip_dates) TextView mDatesView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private void updatePoiList() {
        mPois = mTrip.getPois();
        mPoiListView.swapAdapter(new PoiAdapter(this, mPois), false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // TODO Query by id for now. Change when get to final data model
        mTrip = Trip.getById(getIntent().getLongExtra(Trip.EXTRA_ID_NAME, 0L));
        mCollapsingToolbar.setTitle(mTrip.getTitle());
        mCollapsingToolbar.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbar.setExpandedTitleGravity(Gravity.BOTTOM|Gravity.CENTER);
        mDatesView.setText(mTrip.getDates());

        // TODO Use cursor adapter
        mPois = mTrip.getPois();
        mPoiListView.setHasFixedSize(true);
        mPoiListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PoiAdapter(this, mPois);
        mPoiListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // TODO Add delete, edit, map and share functions
        if (id == R.id.action_add_place) {
            addPlace();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addPlace() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            // TODO Create places filter, I don't need all types
                            //.setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Poi poi = new Poi(place.getId(), place.getName().toString(), mTrip);
                poi.save();
                updatePoiList();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(LOG_TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onRecyclerItemClick(Poi poi) {
        DialogFragment dialog = EditPoiDialog.newInstance(poi.getId());
        dialog.show(getSupportFragmentManager(), "EditPoiDialog");
    }

    @Override
    public void onSaveClick(DialogFragment dialog) {
        updatePoiList();
    }
}
