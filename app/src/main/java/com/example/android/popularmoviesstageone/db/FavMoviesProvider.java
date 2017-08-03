package com.example.android.popularmoviesstageone.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by akshayshahane on 01/07/17.
 */

public class FavMoviesProvider extends ContentProvider {


    public static final int CODE_FAV_MOVIES = 100;
    public static final int CODE_FAV_MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FavMoviesDBHelper mOpenHelper;




    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavMoviesContract.CONTENT_AUTH;

        matcher.addURI(authority, FavMoviesContract.PATH_FAV, CODE_FAV_MOVIES);

        matcher.addURI(authority, FavMoviesContract.PATH_FAV + "/#", CODE_FAV_MOVIE_WITH_ID);

        return matcher;
    }



    @Override
    public boolean onCreate() {
        mOpenHelper = new FavMoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor returnCursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAV_MOVIES:
                returnCursor = db.query(
                        FavMoviesContract.FavMoviesEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1
                );
                break;
            case CODE_FAV_MOVIE_WITH_ID:
                long _id = ContentUris.parseId(uri);
                returnCursor = db.query(
                        FavMoviesContract.FavMoviesEntry.TABLE_NAME,
                        strings,
                        FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID + " =? ",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                       s1
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match =sUriMatcher.match(uri);
        Uri returnUri ;
        
        switch (match){
            
            case CODE_FAV_MOVIES :

                long id = db.insert(FavMoviesContract.FavMoviesEntry.TABLE_NAME,null,contentValues);
                if (id > 0){

                    returnUri = ContentUris.withAppendedId(FavMoviesContract.FavMoviesEntry.CONTENT_URI,id);
                }else {
                    throw new android.database.SQLException("Failed to insert Data"+uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
            
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAV_MOVIES:
                rows = db.delete(FavMoviesContract.FavMoviesEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if ( null == s || rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
}
