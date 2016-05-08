package com.nanodegree.shevchenko.discoverytime.model;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.nanodegree.shevchenko.discoverytime.Util;

/**
 * This class holds places user want to visit during the trip.
 * The name is chosen to not mix it with Place object from Google Places API, I use.
 */
@Table(name = "Poi")
public class Poi extends Model {
    public static final String POI_ID = "PoiId";

    @Column(name = "PlaceId", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    private String mPlaceId;

    @Column(name = "Name")
    private String mName;

    @Column(name = "Trip", onDelete = Column.ForeignKeyAction.CASCADE)
    private Trip mTrip;

    @Column(name = "Note")
    private String mNote;

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
        // 0 means place is not attached to any day
        mDay = 0;
    }

    public static Poi getById(Long id) {
        return new Select()
                .from(Poi.class)
                .where(Cache.getTableInfo(Poi.class).getIdName() + " >= ?", id)
                .executeSingle();
    }

    public String getName() {
        return mName;
    }

    public Trip getTrip() {
        return mTrip;
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

    public String getDateStr() {
        return (mDay == 0) ? "" : Util.getDateByDayNumber(mTrip.getStartDate(), mDay);
    }
}
