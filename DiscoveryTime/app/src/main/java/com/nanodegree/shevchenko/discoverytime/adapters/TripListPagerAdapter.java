package com.nanodegree.shevchenko.discoverytime.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.ui.PageFragment;

public class TripListPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 3;
    private Context mContext;

    public TripListPagerAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position + 1);
    }

    @Override
    public String getPageTitle(int position) {
        // Generate title based on item position
        String title;
        switch(position) {
            case 0:
                title = mContext.getResources().getString(R.string.upcoming);
                break;
            case 1:
                title = mContext.getResources().getString(R.string.wish_list);
                break;
            case 2:
                title = mContext.getResources().getString(R.string.past);
                break;
            default:
                title = null;
                break;
        }
        return title;
    }
}