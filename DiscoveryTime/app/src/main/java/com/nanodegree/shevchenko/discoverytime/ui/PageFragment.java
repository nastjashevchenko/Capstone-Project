package com.nanodegree.shevchenko.discoverytime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.adapters.TripAdapter;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private List<Trip> mTripList = new ArrayList<>();

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
        ListView tripListView = (ListView) view.findViewById(R.id.trip_list);

        if (mPage == 1) mTripList = Trip.getUpcoming();
        if (mPage == 2) mTripList = Trip.getWishList();
        if (mPage == 3) mTripList = Trip.getPast();

        TripAdapter tripAdapter = new TripAdapter(getContext(), mTripList);
        tripListView.setAdapter(tripAdapter);
        tripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent tripActivity = new Intent(getContext(), TripActivity.class);
                //tripActivity.putExtra(Trip.EXTRA_NAME, mTripList.get(position));
                tripActivity.putExtra(Trip.EXTRA_ID_NAME, mTripList.get(position).getId());
                startActivity(tripActivity);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO update list onResume, trips can be added/deleted/changed
    }
}
