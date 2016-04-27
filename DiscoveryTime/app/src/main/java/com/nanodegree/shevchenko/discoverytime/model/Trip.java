package com.nanodegree.shevchenko.discoverytime.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private long mStartDate;
    private long mEndDate;

    public Trip() {};

    public Trip(String title) {
        this.mTitle = title;
    }

    public Trip(String title, long startDate, long endDate) {
        this.mTitle = title;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setStartDate(long startDate) {
        this.mStartDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.mEndDate = endDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getId() {
        return mId;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public long getEndDate() {
        return mEndDate;
    }

    public String getStartDateStr() {
        return dateInMillisToString(mStartDate);
    }

    public String getEndDateStr() {
        return dateInMillisToString(mEndDate);
    }

    private String dateInMillisToString(long dateInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        return dateFormat.format(new Date(dateInMillis));
    }

    public String getDates() {
        if (mStartDate == 0L || mEndDate == 0L) return "";
        return getStartDateStr() + " - " + getEndDateStr();
    }

    public static List<Trip> testUpcoming() {
        List<Trip> upcoming = new ArrayList<>();
        upcoming.add(new Trip("Yosemite", 1463705102470L, 1464741925678L));
        upcoming.add(new Trip("Las Vegas", 1463705102470L, 1464741925678L));
        upcoming.add(new Trip("New York", 1463705102470L, 1464741925678L));
        return upcoming;
    }

    public static List<Trip> testPast() {
        List<Trip> past = new ArrayList<>();
        past.add(new Trip("Yosemite", 1463705102470L, 1464741925678L));
        past.add(new Trip("Las Vegas", 1463705102470L, 1464741925678L));
        past.add(new Trip("New York", 1463705102470L, 1464741925678L));
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
        dest.writeLong(this.mStartDate);
        dest.writeLong(this.mEndDate);
    }

    protected Trip(Parcel in) {
        this.mTitle = in.readString();
        this.mStartDate = in.readLong();
        this.mEndDate = in.readLong();
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
