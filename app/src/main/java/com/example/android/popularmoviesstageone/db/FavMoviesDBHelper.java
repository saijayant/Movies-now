package com.example.android.popularmoviesstageone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by akshayshahane on 01/07/17.
 */

public class FavMoviesDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "fav_movies.db";
    private static final int DATABASE_VERSION = 1;

     public FavMoviesDBHelper (Context context){

         super(context,DATABASE_NAME,null,DATABASE_VERSION);
     }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAV_TABLE =
                "CREATE TABLE " + FavMoviesContract.FavMoviesEntry.TABLE_NAME + " ("+
                        FavMoviesContract.FavMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                        FavMoviesContract.FavMoviesEntry.COLUMN_RDATE + " TEXT NOT NULL, " +
                        FavMoviesContract.FavMoviesEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                        FavMoviesContract.FavMoviesEntry.COLUMN_RATINGS + " TEXT NOT NULL, " +
                        " UNIQUE (" + FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID + " ) ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavMoviesContract.FavMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
