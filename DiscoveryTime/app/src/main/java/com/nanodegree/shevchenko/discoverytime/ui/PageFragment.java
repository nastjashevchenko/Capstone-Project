package com.nanodegree.shevchenko.discoverytime.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.model.Trip;
import com.nanodegree.shevchenko.discoverytime.adapters.TripAdapter;

import java.util.ArrayList;
import java.util.List;

public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

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

        List<Trip> tripList = new ArrayList<>();
        if (mPage == 1) tripList = Trip.getUpcoming();
        if (mPage == 2) tripList = Trip.getWishList();
        if (mPage == 3) tripList = Trip.getPast();

        TripAdapter tripAdapter = new TripAdapter(getContext(), tripList);
        tripListView.setAdapter(tripAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO update list onResume, trips can be added/deleted/changed
    }
}
