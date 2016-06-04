package com.shevchenko.discoverytime.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class TripProvider  extends ContentProvider {
    private static final String TAG = TripProvider.class.getSimpleName();

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "com.shevchenko.discoverytime.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_TRIP = 0;
    private static final int URI_TYPE_TRIP_ID = 1;

    private static final int URI_TYPE_TRIP_PLACE = 2;
    private static final int URI_TYPE_TRIP_PLACE_ID = 3;

    private SQLiteOpenHelper mSqLiteOpenHelper;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, TripContract.TripColumns.TABLE_NAME, URI_TYPE_TRIP);
        URI_MATCHER.addURI(AUTHORITY, TripContract.TripColumns.TABLE_NAME + "/#", URI_TYPE_TRIP_ID);
        URI_MATCHER.addURI(AUTHORITY, TripContract.TripPlaceColumns.TABLE_NAME, URI_TYPE_TRIP_PLACE);
        URI_MATCHER.addURI(AUTHORITY, TripContract.TripPlaceColumns.TABLE_NAME + "/#", URI_TYPE_TRIP_PLACE_ID);
    }

    @Override
    public boolean onCreate() {
        mSqLiteOpenHelper = TripSQLiteOpenHelper.getInstance(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_TRIP:
                return TYPE_CURSOR_DIR + TripContract.TripColumns.TABLE_NAME;
            case URI_TYPE_TRIP_ID:
                return TYPE_CURSOR_ITEM + TripContract.TripColumns.TABLE_NAME;

            case URI_TYPE_TRIP_PLACE:
                return TYPE_CURSOR_DIR + TripContract.TripPlaceColumns.TABLE_NAME;
            case URI_TYPE_TRIP_PLACE_ID:
                return TYPE_CURSOR_ITEM + TripContract.TripPlaceColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = uri.getLastPathSegment();
        long rowId = mSqLiteOpenHelper.getWritableDatabase().insertOrThrow(tableName, null, values);
        if (rowId == -1) return null;
        getContext().getContentResolver().notifyChange(uri, null);
        return uri.buildUpon().appendEncodedPath(String.valueOf(rowId)).build();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        QueryParams queryParams = getQueryParams(uri, selection, null);
        int rowsUpdated = mSqLiteOpenHelper.getWritableDatabase()
                .update(queryParams.table, values, queryParams.selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        QueryParams queryParams = getQueryParams(uri, selection, null);
        int rowsDeleted = mSqLiteOpenHelper.getWritableDatabase()
                .delete(queryParams.table, queryParams.selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        QueryParams queryParams = getQueryParams(uri, selection, projection);
        Cursor res = mSqLiteOpenHelper.getReadableDatabase()
                .query(
                        queryParams.table,
                        projection,
                        queryParams.selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder == null ? queryParams.orderBy : sortOrder);
        res.setNotificationUri(getContext().getContentResolver(), uri);
        return res;
    }

    public static class QueryParams {
        public String table;
        public String idColumn;
        public String selection;
        public String orderBy;
    }

    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_TRIP:
            case URI_TYPE_TRIP_ID:
                res.table = TripContract.TripColumns.TABLE_NAME;
                res.idColumn = TripContract.TripColumns._ID;
                res.orderBy = TripContract.TripColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_TRIP_PLACE:
            case URI_TYPE_TRIP_PLACE_ID:
                res.table = TripContract.TripPlaceColumns.TABLE_NAME;
                res.idColumn = TripContract.TripPlaceColumns._ID;
                res.orderBy = TripContract.TripPlaceColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_TRIP_ID:
            case URI_TYPE_TRIP_PLACE_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
