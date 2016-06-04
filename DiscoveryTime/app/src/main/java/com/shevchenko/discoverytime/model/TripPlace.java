package com.shevchenko.discoverytime.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.shevchenko.discoverytime.data.TripContract;

import java.util.ArrayList;

/**
 * This class holds places user want to visit during the trip.
 * The name is chosen to not mix it with Place object from Google Places API, I use.
 */
public class TripPlace implements Parcelable {
    public static final String TRIP_PLACE = "TripPlace";
    public static final String PLACES_LIST = "TripPlacesList";

    private Long id;
    private String mPlaceId;
    private String mName;
    private Long mTripId;
    private String mNote;
    private Integer mDay;
    private double mLat;
    private double mLng;

    public TripPlace() {
        super();
    }

    public TripPlace(String placeId, String name, Long tripId) {
        super();
        mPlaceId = placeId;
        mName = name;
        mTripId = tripId;
        // 0 means place is not attached to any day
        mDay = 0;
    }

    public TripPlace(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(TripContract.TripPlaceColumns._ID));
        mPlaceId = cursor.getString(cursor.getColumnIndex(TripContract.TripPlaceColumns.PLACE_ID));
        mTripId = cursor.getLong(cursor.getColumnIndex(TripContract.TripPlaceColumns.TRIP_ID));
        mName = cursor.getString(cursor.getColumnIndex(TripContract.TripPlaceColumns.NAME));
        mNote = cursor.getString(cursor.getColumnIndex(TripContract.TripPlaceColumns.NOTE));
        mDay = cursor.getInt(cursor.getColumnIndex(TripContract.TripPlaceColumns.DAY));
        mLat = cursor.getDouble(cursor.getColumnIndex(TripContract.TripPlaceColumns.LAT));
        mLng = cursor.getDouble(cursor.getColumnIndex(TripContract.TripPlaceColumns.LNG));
    }

    public static ArrayList<TripPlace> createListFromCursor(Cursor cursor) {
        ArrayList<TripPlace> places = new ArrayList<>();
        if (cursor == null || cursor.getCount() <= 0) return places;
        cursor.moveToFirst();
        do {
            places.add(new TripPlace(cursor));
        } while (cursor.moveToNext());
        return places;
    }

    private ContentValues putPlaceToValues() {
        ContentValues values = new ContentValues();
        values.put(TripContract.TripPlaceColumns.PLACE_ID, mPlaceId);
        values.put(TripContract.TripPlaceColumns.TRIP_ID, mTripId);
        values.put(TripContract.TripPlaceColumns.NAME, mName);
        values.put(TripContract.TripPlaceColumns.NOTE, mNote);
        values.put(TripContract.TripPlaceColumns.DAY, mDay);
        values.put(TripContract.TripPlaceColumns.LAT, mLat);
        values.put(TripContract.TripPlaceColumns.LNG, mLng);
        return values;
    }

    public void save(ContentResolver resolver) {
        if (id != null) {
            resolver.update(TripContract.TripPlaceColumns.CONTENT_URI,
                    putPlaceToValues(),
                    TripContract.TripPlaceColumns._ID + " = ?",
                    new String[]{id.toString()});
        } else {
            resolver.insert(TripContract.TripPlaceColumns.CONTENT_URI,
                    putPlaceToValues());
        }
    }

    public void delete(ContentResolver resolver) {
        resolver.delete(TripContract.TripPlaceColumns.CONTENT_URI,
                TripContract.TripPlaceColumns._ID + " = ?",
                new String[]{id.toString()});
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return mName;
    }

    public Integer getDay() {
        return mDay;
    }

    public void setDay(Integer day) {
        this.mDay = day;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        this.mNote = note;
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

    public Long getTripId() {
        return mTripId;
    }

    public void setLng(double lng) {
        this.mLng = lng;
    }

    // ---- Parcelable methods ----

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.mTripId);
        dest.writeString(this.mPlaceId);
        dest.writeString(this.mName);
        dest.writeString(this.mNote);
        dest.writeValue(this.mDay);
        dest.writeDouble(this.mLat);
        dest.writeDouble(this.mLng);
    }

    protected TripPlace(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.mTripId = (Long) in.readValue(Long.class.getClassLoader());
        this.mPlaceId = in.readString();
        this.mName = in.readString();
        this.mNote = in.readString();
        this.mDay = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mLat = in.readDouble();
        this.mLng = in.readDouble();
    }

    public static final Creator<TripPlace> CREATOR = new Creator<TripPlace>() {
        @Override
        public TripPlace createFromParcel(Parcel source) {
            return new TripPlace(source);
        }

        @Override
        public TripPlace[] newArray(int size) {
            return new TripPlace[size];
        }
    };

    public void insertWithTripId(Long tripId, ContentResolver resolver) {
        mTripId = tripId;
        resolver.insert(TripContract.TripPlaceColumns.CONTENT_URI,
                putPlaceToValues());
    }
}
