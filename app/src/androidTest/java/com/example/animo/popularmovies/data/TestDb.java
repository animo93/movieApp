package com.example.animo.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import junit.framework.Test;

import java.util.HashSet;

/**
 * Created by animo on 1/10/16.
 */
public class TestDb extends AndroidTestCase{
    public void testMoviesTable() {
        MoviesDbHelper moviesDbHelper=new MoviesDbHelper(mContext);
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet= new HashSet<>();
        tableNameHashSet.add(MoviesContract.FavMovies.TABLE_NAME);

        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);

        SQLiteDatabase db=new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor=db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null);

        assertTrue("Error: The database is not created",cursor.moveToFirst());

        do{
            tableNameHashSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        assertTrue("Database doesn't have Movies table",tableNameHashSet.isEmpty());

        cursor=db.rawQuery("PRAGMA table_info(" +MoviesContract.FavMovies.TABLE_NAME+ ")",null);

        Log.e("inside Test Db", "column count is " + cursor.getColumnCount());

        assertTrue("Error :This means we are unable to query the database for table info ", cursor.moveToFirst());

        final HashSet<String> moviesColumnHashSet= new HashSet<>();
        moviesColumnHashSet.add(MoviesContract.FavMovies._ID);
        moviesColumnHashSet.add(MoviesContract.FavMovies.COLUMN_DATE);
        moviesColumnHashSet.add(MoviesContract.FavMovies.COLUMN_OVERVIEW);
        moviesColumnHashSet.add(MoviesContract.FavMovies.COLUMN_POSTER_PATH);
        moviesColumnHashSet.add(MoviesContract.FavMovies.COLUMN_RATING);
        moviesColumnHashSet.add(MoviesContract.FavMovies.COLUMN_TITLE);
        moviesColumnHashSet.add(MoviesContract.FavMovies.COLUMN_TIME);

        int columnNameIndex=cursor.getColumnIndex("name");
        do{
            String columnName=cursor.getString(columnNameIndex);
            moviesColumnHashSet.remove(columnName);
        }while (cursor.moveToNext());

        assertTrue("Error : The database doesn't contail the req database", moviesColumnHashSet.isEmpty());

        long moviesId=insertMovies();
        assertFalse("Error, Movies not inserted properly", moviesId == -1L);

        Cursor moviesCursor=db.query(
                MoviesContract.FavMovies.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("Error no records returned from query ", moviesCursor.moveToFirst());

        ContentValues contentValues= TestUtilities.createMovieValues();

        TestUtilities.validateCurrentRecord("Movies entry failed to validate",moviesCursor,contentValues);
        db.close();


    }

    public long insertMovies(){
        MoviesDbHelper moviesDbHelper=new MoviesDbHelper(mContext);
        SQLiteDatabase db=moviesDbHelper.getWritableDatabase();
        ContentValues testValues=TestUtilities.createMovieValues();
        long moviesId=db.insert(MoviesContract.FavMovies.TABLE_NAME,null,testValues);
        return moviesId;

    }
}
