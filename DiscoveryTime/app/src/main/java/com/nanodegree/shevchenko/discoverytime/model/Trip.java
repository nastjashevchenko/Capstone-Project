package com.nanodegree.shevchenko.discoverytime.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.nanodegree.shevchenko.discoverytime.Util;

import java.util.List;

@Table(name = "Trip")
public class Trip extends Model implements Parcelable {
    public static final String EXTRA_NAME = "TRIP";
    public static final String EXTRA_ID_NAME = "id";

    @Column(name = "PlaceId")
    private String mPlaceId;

    @Column(name = "Title")
    private String mTitle;

    @Column(name = "StartDate")
    private long mStartDate;

    @Column(name = "EndDate")
    private long mEndDate;

    @Column(name = "Lat")
    private double mLat;

    @Column(name = "Lng")
    private double mLng;

    public Trip() {
        super();
    };

    // ---- Getters and Setters ----

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setStartDate(long startDate) {
        this.mStartDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.mEndDate = endDate;
    }

    public void setPlaceId(String id) {
        this.mPlaceId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public long getEndDate() {
        return mEndDate;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        this.mLat = lat;
    }

    public double getLng() {
        return mLng;
    }

    public void setLng(double lng) {
        this.mLng = lng;
    }

    public String getStartDateStr() {
        return Util.longDateToString(mStartDate);
    }

    public String getEndDateStr() {
        return Util.longDateToString(mEndDate);
    }

    public String getDates(String tmpl) {
        if (mStartDate == 0L || mEndDate == 0L) return "";
        return String.format(tmpl, getStartDateStr(), getEndDateStr());
    }

    // ---- DB queries ----
    public static Trip getById(Long id) {
        return new Select()
                .from(Trip.class)
                .where(Cache.getTableInfo(Trip.class).getIdName() + " >= ?", id)
                .executeSingle();
    }

    public static List<Trip> getUpcoming() {
        return new Select()
                .from(Trip.class)
                .where("EndDate >= ?", System.currentTimeMillis())
                .orderBy("StartDate ASC")
                .execute();
    }

    public static List<Trip> getPast() {
        return new Select()
                .from(Trip.class)
                .where("EndDate != ?", 0L)
                .where("EndDate < ?", System.currentTimeMillis())
                .orderBy("StartDate DESC")
                .execute();
    }

    public static List<Trip> getWishList() {
        return new Select()
                .from(Trip.class)
                .where("StartDate = ?", 0L)
                .execute();
    }

    public List<Poi> getPois() {
        return new Select().from(Poi.class)
                .where(Cache.getTableName(Poi.class) + "." + "Trip" + "=?", getId())
                .orderBy("Day ASC")
                .execute();
    }

    // ---- Parcelable methods ----

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPlaceId);
        dest.writeString(this.mTitle);
        dest.writeLong(this.mStartDate);
        dest.writeLong(this.mEndDate);
    }

    protected Trip(Parcel in) {
        this.mPlaceId = in.readString();
        this.mTitle = in.readString();
        this.mStartDate = in.readLong();
        this.mEndDate = in.readLong();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel source) {
            return new Trip(source);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public List<String> getAllDates() {
        return Util.getDaysBetweenDates(mStartDate, mEndDate);
    }
}
