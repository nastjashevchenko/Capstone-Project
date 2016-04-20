package com.nanodegree.shevchenko.discoverytime.model;


import java.util.ArrayList;
import java.util.List;

public class Trip {
    private String mTitle;
    private String mStartDate;
    private String mEndDate;

    public Trip(String title) {
        this.mTitle = title;
    }

    public Trip(String title, String startDate, String endDate) {
        this.mTitle = title;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
    }

    public void setDates(String startDate, String endDate) {
        this.mStartDate = startDate;
        this.mEndDate = endDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDates() {
        return (mStartDate != null && mEndDate != null) ? mStartDate + " - " + mEndDate : "";
    }

    public static List<Trip> testUpcoming() {
        List<Trip> upcoming = new ArrayList<>();
        upcoming.add(new Trip("Yosemite", "06/06/2016", "06/08/2016"));
        upcoming.add(new Trip("Las Vegas", "07/06/2016", "07/08/2016"));
        upcoming.add(new Trip("New York", "06/06/2016", "06/08/2016"));
        return upcoming;
    }

    public static List<Trip> testPast() {
        List<Trip> past = new ArrayList<>();
        past.add(new Trip("Yosemite", "06/06/2015", "06/08/2015"));
        past.add(new Trip("Las Vegas", "07/06/2015", "07/08/2015"));
        past.add(new Trip("New York", "06/06/2015", "06/08/2015"));
        return past;
    }

    public static List<Trip> testWishList() {
        List<Trip> wishlist = new ArrayList<>();
        wishlist.add(new Trip("Ireland"));
        wishlist.add(new Trip("Hollywood"));
        wishlist.add(new Trip("Mexico"));
        wishlist.add(new Trip("Moscow, Russia"));
        wishlist.add(new Trip("Tokyo"));
        return wishlist;
    }
}
