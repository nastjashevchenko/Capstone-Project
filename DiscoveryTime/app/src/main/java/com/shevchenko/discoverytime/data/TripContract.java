package com.shevchenko.discoverytime.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class TripContract {

    public static final class TripColumns implements BaseColumns {
        public static final String TABLE_NAME = "trip";
        public static final Uri CONTENT_URI = Uri.parse(TripProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

        public static final String _ID = BaseColumns._ID;
        public static final String PLACE_ID = "trip_place_id";
        public static final String TITLE = "title";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String LAT = "trip_lat";
        public static final String LNG = "trip_lng";

        public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;
    }


    public static final class TripPlaceColumns implements BaseColumns {
        public static final String TABLE_NAME = "trip_place";
        public static final Uri CONTENT_URI = Uri.parse(TripProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

        public static final String _ID = BaseColumns._ID;
        public static final String PLACE_ID = "place_id";
        public static final String NAME = "name";
        public static final String NOTE = "note";
        public static final String DAY = "day";
        public static final String LAT = "place_lat";
        public static final String LNG = "place_lng";
        public static final String TRIP_ID = "trip_id";

        public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;
    }
}
