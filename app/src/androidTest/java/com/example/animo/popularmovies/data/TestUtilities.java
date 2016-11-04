package com.example.animo.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by animo on 1/10/16.
 */
public class TestUtilities extends AndroidTestCase {

    static ContentValues createMovieValues(){
        ContentValues movieValues=new ContentValues();
        movieValues.put(MoviesContract.FavMovies.COLUMN_DATE,"24-06-2016");
        movieValues.put(MoviesContract.FavMovies.COLUMN_OVERVIEW,"overview");
        movieValues.put(MoviesContract.FavMovies.COLUMN_POSTER_PATH,"abcd.jpg");
        movieValues.put(MoviesContract.FavMovies.COLUMN_RATING,"5");
        movieValues.put(MoviesContract.FavMovies.COLUMN_TITLE,"The Rock");
        movieValues.put(MoviesContract.FavMovies.COLUMN_TIME,"14:05");

        return movieValues;

    }
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
