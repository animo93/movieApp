package com.example.animo.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by animo on 30/9/16.
 */
public class TestMoviesContract extends AndroidTestCase{

    private static final String TEST_MOVIES="/The Rock";

    public void testMoviesPath(){
        Uri uri= MoviesContract.FavMovies.buildMoviePath(TEST_MOVIES);
        assertNotNull("Error Null Uri returned",uri);
        assertEquals("Error path not properly appended", TEST_MOVIES, uri.getLastPathSegment());
        assertEquals("Error Uri doesn't match the pattern",uri.toString(),
                "content://com.example.animo.popularmovies/movies/%2FThe%20Rock");
    }

}
