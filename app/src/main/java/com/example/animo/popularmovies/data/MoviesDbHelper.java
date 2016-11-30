package com.example.animo.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by animo on 29/9/16.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=4;

    static final String DATABASE_NAME="movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE= "CREATE TABLE "+ MoviesContract.FavMovies.TABLE_NAME+ " ("+
                MoviesContract.FavMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                MoviesContract.FavMovies.COLUMN_MOVIE_ID + " TEXT NOT NULL, "+
                MoviesContract.FavMovies.COLUMN_DATE + " TEXT NOT NULL, " +
                MoviesContract.FavMovies.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.FavMovies.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.FavMovies.COLUMN_RATING + " TEXT NOT NULL," +
                MoviesContract.FavMovies.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MoviesContract.FavMovies.COLUMN_TIME + " TEXT NOT NULL, " +
                MoviesContract.FavMovies.COLUMN_TITLE + " TEXT NOT NULL " +
                " );";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ MoviesContract.FavMovies.TABLE_NAME);
        onCreate(db);

    }
}
