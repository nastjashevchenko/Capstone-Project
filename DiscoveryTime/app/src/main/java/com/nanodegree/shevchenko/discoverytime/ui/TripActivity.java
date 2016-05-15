package com.nanodegree.shevchenko.discoverytime.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.nanodegree.shevchenko.discoverytime.PhotoTask;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.adapters.PlaceAdapter;
import com.nanodegree.shevchenko.discoverytime.model.Trip;
import com.nanodegree.shevchenko.discoverytime.model.TripPlace;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripActivity extends AppCompatActivity
        implements PlaceAdapter.OnRecyclerItemClickListener, EditPlaceDialog.EditPlaceDialogListener,
                   EditTripDialog.EditTripDialogListener, GoogleApiClient.OnConnectionFailedListener {

    private Trip mTrip;
    private ArrayList<TripPlace> mTripPlaces;
    private PlaceAdapter mAdapter;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;
    private static final String LOG_TAG = TripActivity.class.getName();
    GoogleApiClient mGoogleApiClient;

    @BindView(R.id.place_list) RecyclerView mPlaceListView;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.trip_dates) TextView mDatesView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.trip_image) ImageView mImageView;
    @BindView(R.id.empty) TextView mEmptyListView;

    // TODO Need to update place list on resume when place was changed from map activity
    private void updatePlaceList() {
        mTripPlaces = mTrip.getPlaces(getContentResolver());
        mEmptyListView.setVisibility(mTripPlaces.size() > 0 ? View.GONE : View.VISIBLE);
        mAdapter.setUpdatedList(mTripPlaces);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        mTrip = getIntent().getParcelableExtra(Trip.EXTRA_NAME);
        mCollapsingToolbar.setTitle(mTrip.getTitle());
        mCollapsingToolbar.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbar.setExpandedTitleGravity(Gravity.BOTTOM | Gravity.CENTER);
        mDatesView.setText(mTrip.getDates(getResources().getString(R.string.from_to_tmpl)));

        // TODO Use cursor adapter
        mTripPlaces = mTrip.getPlaces(getContentResolver());
        mEmptyListView.setVisibility(mTripPlaces.size() > 0 ? View.GONE : View.VISIBLE);
        mPlaceListView.setHasFixedSize(true);
        mPlaceListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceAdapter(this, mTripPlaces, mTrip.getStartDate());
        mPlaceListView.setAdapter(mAdapter);

        placePhotosTask();
    }

    private void placePhotosTask() {
        new PhotoTask(getApplicationContext(), mGoogleApiClient, mTrip.getPlaceId(),
                mImageView.getWidth(), mImageView.getHeight()) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                mImageView.setImageResource(R.drawable.test_image);
            }

            @Override
            protected void onPostExecute(Bitmap photo) {
                if (photo != null) {
                    // Photo has been loaded, display it.
                    mImageView.setImageBitmap(photo);
                }
            }
        }.execute(mTrip.getLat(), mTrip.getLng());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // TODO if mTripPlaces list is empty - hide this button
        if (id == R.id.action_show_on_map && (mTripPlaces != null && mTripPlaces.size() > 0)) {
            Intent mapActivity = new Intent(this, MapActivity.class);
            mapActivity.putParcelableArrayListExtra(TripPlace.PLACES_LIST, mTripPlaces);
            mapActivity.putExtra(Trip.START_DATE, mTrip.getStartDate());
            mapActivity.putExtra(Trip.END_DATE, mTrip.getEndDate());
            startActivity(mapActivity);
            return true;
        }
        if (id == R.id.action_edit) {
            DialogFragment dialog = EditTripDialog.newInstance(mTrip);
            dialog.show(getSupportFragmentManager(), "EditTripDialog");
            return true;
        }
        if (id == R.id.action_delete) {
            // TODO add UNDO snackbar
            mTrip.delete(getContentResolver());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addPlace(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                TripPlace tripPlace = new TripPlace(place.getId(), place.getName().toString(),
                        mTrip.getId());
                tripPlace.setLat(place.getLatLng().latitude);
                tripPlace.setLng(place.getLatLng().longitude);
                tripPlace.save(getContentResolver());
                updatePlaceList();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(LOG_TAG, status.getStatusMessage());
            }
        }
    }

    @Override
    public void onRecyclerItemClick(TripPlace place) {
        DialogFragment dialog = EditPlaceDialog.newInstance(place,
                mTrip.getStartDate(), mTrip.getEndDate());
        dialog.show(getSupportFragmentManager(), "EditPlaceDialog");
    }

    @Override
    public void onTitleChanged(DialogFragment dialog) {
        mCollapsingToolbar.setTitle(mTrip.getTitle());
    }

    @Override
    public void onDatesChanged(DialogFragment dialog) {
        mDatesView.setText(mTrip.getDates(getResources().getString(R.string.from_to_tmpl)));
        updatePlaceList();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSaveClick(DialogFragment dialog, TripPlace place) {
        updatePlaceList();
    }
}
