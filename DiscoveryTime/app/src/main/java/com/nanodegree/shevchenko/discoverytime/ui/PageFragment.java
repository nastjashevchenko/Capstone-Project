package com.nanodegree.shevchenko.discoverytime.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.adapters.TripCursorAdapter;
import com.nanodegree.shevchenko.discoverytime.data.TripContract;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PageFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ARG_PAGE = "ARG_PAGE";
    private static final int URL_LOADER = 0;

    @BindView(R.id.trip_list) ListView mTripListView;
    @BindView(R.id.empty) TextView mEmptyListView;

    private int mPage;
    TripCursorAdapter tripAdapter;
    Cursor mCursor = null;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        ButterKnife.bind(this, view);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        tripAdapter = new TripCursorAdapter(getContext(), null);
        mTripListView.setAdapter(tripAdapter);
        mTripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent tripActivity = new Intent(getContext(), TripActivity.class);
                if (mCursor.moveToPosition(position)) {
                    Trip trip = new Trip(mCursor);
                    tripActivity.putExtra(Trip.EXTRA_NAME, trip);
                    startActivity(tripActivity);
                }
            }
        });
        mTripListView.setEmptyView(mEmptyListView);
        return view;
    }

    // -- Cursor Loader Callback methods

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                String clause = null;
                String[] clauseArgs = null;
                String order = null;
                switch(mPage) {
                    case 1:
                        clause = TripContract.TripColumns.END_DATE + " >= ?";
                        clauseArgs = new String[]{String.valueOf(System.currentTimeMillis())};
                        order = TripContract.TripColumns.START_DATE + " ASC";
                        break;
                    case 2:
                        clause = TripContract.TripColumns.START_DATE + " = ?";
                        clauseArgs = new String[]{"0"};
                        break;
                    case 3:
                        clause = TripContract.TripColumns.END_DATE + " != ? AND "
                                + TripContract.TripColumns.END_DATE + " < ?";
                        clauseArgs = new String[]{"0", String.valueOf(System.currentTimeMillis())};
                        order = TripContract.TripColumns.START_DATE + " DESC";
                        break;
                    default:
                        break;
                }
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        TripContract.TripColumns.CONTENT_URI,        // Table to query
                        null,     // Projection to return
                        clause,            // Selection clause
                        clauseArgs,            // Selection arguments
                        order             // Sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        tripAdapter.swapCursor(data);
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        tripAdapter.swapCursor(null);
        mCursor = null;
    }
}
