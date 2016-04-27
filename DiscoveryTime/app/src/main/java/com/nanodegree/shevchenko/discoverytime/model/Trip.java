package com.nanodegree.shevchenko.discoverytime.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Trip implements Parcelable {
    private String mId;

    public String getDefaultTitle() {
        return mDefaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.mDefaultTitle = defaultTitle;
    }

    private String mDefaultTitle;
    private String mTitle;
    private String mStartDate;
    private String mEndDate;

    public Trip() {};

    public Trip(String title) {
        this.mTitle = title;
    }

    public Trip(String title, String startDate, String endDate) {
        this.mTitle = title;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setDateStart(String startDate) {
        this.mStartDate = startDate;
    }

    public void setDateEnd(String endDate) {
        this.mEndDate = endDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getId() {
        return mId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
        dest.writeString(this.mStartDate);
        dest.writeString(this.mEndDate);
    }

    protected Trip(Parcel in) {
        this.mTitle = in.readString();
        this.mStartDate = in.readString();
        this.mEndDate = in.readString();
    }

    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel source) {
            return new Trip(source);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public void setId(String id) {
        this.mId = id;
    }
}
