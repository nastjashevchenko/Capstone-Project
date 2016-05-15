package com.nanodegree.shevchenko.discoverytime.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.nanodegree.shevchenko.discoverytime.BuildConfig;

public class TripSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = TripSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "trips.db";
    private static final int DATABASE_VERSION = 1;
    private static TripSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final TripSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_TRIP = "CREATE TABLE IF NOT EXISTS "
            + TripContract.TripColumns.TABLE_NAME + " ( "
            + TripContract.TripColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TripContract.TripColumns.PLACE_ID + " TEXT, "
            + TripContract.TripColumns.TITLE + " TEXT, "
            + TripContract.TripColumns.START_DATE + " INTEGER, "
            + TripContract.TripColumns.END_DATE + " INTEGER, "
            + TripContract.TripColumns.LAT + " REAL, "
            + TripContract.TripColumns.LNG + " REAL "
            + " );";

    public static final String SQL_CREATE_TABLE_TRIP_PLACE = "CREATE TABLE IF NOT EXISTS "
            + TripContract.TripPlaceColumns.TABLE_NAME + " ( "
            + TripContract.TripPlaceColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TripContract.TripPlaceColumns.PLACE_ID + " TEXT, "
            + TripContract.TripPlaceColumns.NAME + " TEXT, "
            + TripContract.TripPlaceColumns.NOTE + " TEXT, "
            + TripContract.TripPlaceColumns.DAY + " INTEGER, "
            + TripContract.TripPlaceColumns.LAT + " REAL, "
            + TripContract.TripPlaceColumns.LNG + " REAL, "
            + TripContract.TripPlaceColumns.TRIP_ID + " INTEGER "
            + ", CONSTRAINT fk_trip_id FOREIGN KEY (" + TripContract.TripPlaceColumns.TRIP_ID + ") REFERENCES trip (_id) ON DELETE CASCADE"
            + " );";

    // @formatter:on

    public static TripSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static TripSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static TripSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new TripSQLiteOpenHelper(context);
    }

    private TripSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new TripSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static TripSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new TripSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private TripSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new TripSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_TRIP);
        db.execSQL(SQL_CREATE_TABLE_TRIP_PLACE);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
