package com.nanodegree.shevchenko.discoverytime.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * This class holds places user want to visit during the trip.
 * The name is chosen to not mix it with Place object from Google Places API, I use.
 */
@Table(name = "Poi")
public class Poi extends Model {
    @Column(name = "PlaceId", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    private String mPlaceId;

    @Column(name = "Name")
    private String mName;

    @Column(name = "Trip")
    private Trip mTrip;

    public Integer getDay() {
        return mDay;
    }

    public void setDay(Integer mDay) {
        this.mDay = mDay;
    }

    @Column(name = "Day")
    private Integer mDay;

    public Poi() {
        super();
    }

    public Poi(String placeId, String name, Trip trip) {
        super();
        mPlaceId = placeId;
        mName = name;
        mTrip = trip;
        // -1 means place is not attached to any day
        mDay = -1;
    }

    public String getName() {
        return mName;
    }
}
