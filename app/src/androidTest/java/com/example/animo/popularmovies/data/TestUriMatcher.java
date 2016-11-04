package com.example.animo.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by animo on 21/10/16.
 */
public class TestUriMatcher extends AndroidTestCase{

    private static final Uri TEST_MOVIES_DIR=MoviesContract.FavMovies.CONTENT_URI;
    private static final Uri TEST_MOVIES_WITH_ID=MoviesContract.FavMovies.buildMovieUri(1L);

    public void testUriMatcher(){
        UriMatcher testUriMatcher=MoviesProvider.buildUriMatcher();

        assertEquals("Error , the movies uri is not matched ",testUriMatcher.match(TEST_MOVIES_DIR),MoviesProvider.MOVIES);
        assertEquals("Error , the movies with id uri is not matched", testUriMatcher.match(TEST_MOVIES_WITH_ID),MoviesProvider.MOVIES_WITH_IDS);
    }
}
