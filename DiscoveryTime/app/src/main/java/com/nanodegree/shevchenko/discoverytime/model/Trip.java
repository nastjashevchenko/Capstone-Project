package com.nanodegree.shevchenko.discoverytime.model;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.nanodegree.shevchenko.discoverytime.Util;
import com.nanodegree.shevchenko.discoverytime.data.TripContract;

public class Trip implements Parcelable {
    public static final String EXTRA_NAME = "TRIP";
    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";

    private Long id;
    private String mPlaceId;
    private String mTitle;
    private Long mStartDate;
    private Long mEndDate;
    private Double mLat;
    private Double mLng;

    public Trip() {
        super();
    };

    public Trip(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(TripContract.TripColumns._ID));
        mPlaceId = cursor.getString(cursor.getColumnIndex(TripContract.TripColumns.PLACE_ID));
        mTitle = cursor.getString(cursor.getColumnIndex(TripContract.TripColumns.TITLE));
        mStartDate = cursor.getLong(cursor.getColumnIndex(TripContract.TripColumns.START_DATE));
        mEndDate = cursor.getLong(cursor.getColumnIndex(TripContract.TripColumns.END_DATE));
        mLat = cursor.getDouble(cursor.getColumnIndex(TripContract.TripColumns.LAT));
        mLng = cursor.getDouble(cursor.getColumnIndex(TripContract.TripColumns.LNG));
    }

    // ---- Getters and Setters ----
    public Long getId() {
        return id;
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

    public void setPlaceId(String id) {
        this.mPlaceId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public Long getStartDate() {
        return mStartDate;
    }

    public Long getEndDate() {
        return mEndDate;
    }

    public Double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        this.mLat = lat;
    }

    public Double getLng() {
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

    private ContentValues putTripToValues() {
        ContentValues values = new ContentValues();
        values.put(TripContract.TripColumns.PLACE_ID, mPlaceId);
        values.put(TripContract.TripColumns.TITLE, mTitle);
        values.put(TripContract.TripColumns.START_DATE, mStartDate);
        values.put(TripContract.TripColumns.END_DATE, mEndDate);
        values.put(TripContract.TripColumns.LAT, mLat);
        values.put(TripContract.TripColumns.LNG, mLng);
        return values;
    }

    public void save(ContentResolver resolver) {
        // if id != null ( != 0L) -> update, else insert
        if (id != null) {
            resolver.update(TripContract.TripColumns.CONTENT_URI,
                    putTripToValues(),
                    TripContract.TripColumns._ID + " = ?",
                    new String[]{id.toString()});
        } else {
            resolver.insert(TripContract.TripColumns.CONTENT_URI,
                    putTripToValues());
        }
    }

    public void delete(ContentResolver resolver) {
        resolver.delete(TripContract.TripColumns.CONTENT_URI,
                TripContract.TripColumns._ID + " = ?",
                new String[]{id.toString()});
    }

    /** If duration became shorter, some places could have days out of new range
     ** This places will be not assigned to any day in this case
     **/
    public void updatePlacesDays(ContentResolver resolver, Long daysCount) {
        ContentValues values = new ContentValues();
        values.put(TripContract.TripPlaceColumns.DAY, 0);
        resolver.update(
                TripContract.TripPlaceColumns.CONTENT_URI,
                values,
                TripContract.TripPlaceColumns.TRIP_ID + " = ? AND "
                + TripContract.TripPlaceColumns.DAY + " > ?",
                new String[]{id.toString(), daysCount.toString()});
    }

    // ---- Parcelable methods ----

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.mPlaceId);
        dest.writeString(this.mTitle);
        dest.writeValue(this.mStartDate);
        dest.writeValue(this.mEndDate);
        dest.writeValue(this.mLat);
        dest.writeValue(this.mLng);
    }

    protected Trip(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.mPlaceId = in.readString();
        this.mTitle = in.readString();
        this.mStartDate = (Long) in.readValue(Long.class.getClassLoader());
        this.mEndDate = (Long) in.readValue(Long.class.getClassLoader());
        this.mLat = (Double) in.readValue(Double.class.getClassLoader());
        this.mLng = (Double) in.readValue(Double.class.getClassLoader());
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
}
