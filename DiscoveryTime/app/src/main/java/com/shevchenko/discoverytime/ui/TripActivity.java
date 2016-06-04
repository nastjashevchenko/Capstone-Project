package com.shevchenko.discoverytime.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
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
import com.shevchenko.discoverytime.PhotoTask;
import com.shevchenko.discoverytime.R;
import com.shevchenko.discoverytime.adapters.PlaceAdapter;
import com.shevchenko.discoverytime.data.TripContract;
import com.shevchenko.discoverytime.model.Trip;
import com.shevchenko.discoverytime.model.TripPlace;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripActivity extends AppCompatActivity implements
        PlaceAdapter.OnRecyclerItemClickListener,
        EditTripDialog.EditTripDialogListener,
        GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private Trip mTrip;
    private Cursor mPlacesCursor = null;
    private PlaceAdapter mAdapter;
    private MenuItem mMapMenuItem;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;
    private static final int URL_LOADER = 1;

    private static final String LOG_TAG = TripActivity.class.getName();
    GoogleApiClient mGoogleApiClient;

    @BindView(R.id.place_list) RecyclerView mPlaceListView;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.trip_dates) TextView mDatesView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.trip_image) ImageView mImageView;
    @BindView(R.id.empty) TextView mEmptyListView;

    private void updatePlaceList(Cursor cursor) {
        mEmptyListView.setVisibility((cursor != null && cursor.getCount() > 0) ?
                View.GONE : View.VISIBLE);
        mAdapter.setUpdatedList(cursor);
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

        mPlaceListView.setHasFixedSize(true);
        mPlaceListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceAdapter(this, mPlacesCursor, mTrip.getStartDate());
        mPlaceListView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        placePhotosTask();
    }

    private void placePhotosTask() {
        int size = (int) getResources().getDimension(R.dimen.full_image);
        new PhotoTask(getApplicationContext(), mGoogleApiClient, mTrip.getPlaceId(), size, size) {
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
        mMapMenuItem = menu.findItem(R.id.action_show_on_map);
        mMapMenuItem.setVisible(mPlacesCursor!=null && mPlacesCursor.getCount() > 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_show_on_map:
                Intent mapActivity = new Intent(this, MapActivity.class);
                ArrayList<TripPlace> places = TripPlace.createListFromCursor(mPlacesCursor);
                mapActivity.putParcelableArrayListExtra(TripPlace.PLACES_LIST, places);
                mapActivity.putExtra(Trip.START_DATE, mTrip.getStartDate());
                mapActivity.putExtra(Trip.END_DATE, mTrip.getEndDate());
                startActivity(mapActivity);
                break;
            case R.id.action_edit:
                DialogFragment dialog = EditTripDialog.newInstance(mTrip);
                dialog.show(getSupportFragmentManager(), "EditTripDialog");
                break;
            case R.id.action_delete:
                Intent messageIntent = new Intent(MainActivity.DELETE_EVENT);
                messageIntent.putExtra(Trip.EXTRA_NAME, mTrip);
                messageIntent.putParcelableArrayListExtra(TripPlace.PLACES_LIST,
                        TripPlace.createListFromCursor(mPlacesCursor));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
                mTrip.delete(getContentResolver());
                finish();
                break;
            default:
                break;
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
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // -- Cursor Loader Callback methods

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                return new CursorLoader(
                        this,
                        TripContract.TripPlaceColumns.CONTENT_URI,
                        null,
                        TripContract.TripPlaceColumns.TRIP_ID + " = ?",
                        new String[]{mTrip.getId().toString()},
                        TripContract.TripPlaceColumns.DAY + " ASC"
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPlacesCursor = data;
        updatePlaceList(data);
        if (mMapMenuItem != null) mMapMenuItem.setVisible(data != null && data.getCount() > 0);
        invalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPlacesCursor = null;
        updatePlaceList(null);
    }
}
