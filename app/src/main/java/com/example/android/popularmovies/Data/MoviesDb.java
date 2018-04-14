package com.example.android.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by virat on 12/03/16.
 */
public class MoviesDb extends SQLiteOpenHelper {

    public static final String LOG_TAG= MoviesDb.class.getSimpleName();

    public static final String name= "movies.db";
    public static final int version=1;

    public MoviesDb(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE= "CREATE TABLE " +
                MoviesContract.MoviesEntry.TABLE_NAME+ "(" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_POSTER + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_SYNOPSIS + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_RATING + " INTEGER, " +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");

        //Drop table
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);

        // re-create database
        onCreate(db);
    }
}
