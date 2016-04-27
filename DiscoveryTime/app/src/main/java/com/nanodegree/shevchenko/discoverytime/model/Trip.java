package com.nanodegree.shevchenko.discoverytime.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.nanodegree.shevchenko.discoverytime.Util;

import java.util.List;

@Table(name = "Trip")
public class Trip extends Model implements Parcelable {
    @Column(name = "PlaceId")
    private String mPlaceId;

    @Column(name = "DefaultTitle")
    private String mDefaultTitle;

    @Column(name = "Title")
    private String mTitle;

    @Column(name = "StartDate")
    private long mStartDate;

    @Column(name = "EndDate")
    private long mEndDate;

    public String getDefaultTitle() {
        return mDefaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.mDefaultTitle = defaultTitle;
    }

    public Trip() {
        super();
    };

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

    public String getPlaceId() {
        return mPlaceId;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public long getEndDate() {
        return mEndDate;
    }

    public String getStartDateStr() {
        return Util.longDateToString(mStartDate);
    }

    public String getEndDateStr() {
        return Util.longDateToString(mEndDate);
    }

    public String getDates() {
        if (mStartDate == 0L || mEndDate == 0L) return "";
        return getStartDateStr() + " - " + getEndDateStr();
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

    public void setPlaceId(String id) {
        this.mPlaceId = id;
    }
}
