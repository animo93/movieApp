package com.example.animo.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AndroidException;

/**
 * Created by animo on 11/10/16.
 */
public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher= buildUriMatcher();

    static final int MOVIES=100;
    static final int MOVIES_WITH_IDS=102;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        final String authority=MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority,MoviesContract.PATH_MOVIES,MOVIES);
        uriMatcher.addURI(authority,MoviesContract.PATH_MOVIES+"/#",MOVIES_WITH_IDS);

        return uriMatcher;
    }

    private MoviesDbHelper moviesDbHelper;
    @Override
    public boolean onCreate() {
        moviesDbHelper=new MoviesDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                retCursor=moviesDbHelper.getReadableDatabase().query(MoviesContract.FavMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIES_WITH_IDS: {
                retCursor=getMoviesByMovieId(uri,projection,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Uri not matching "+uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    private Cursor getMoviesByMovieId(Uri uri, String[] projection, String sortOrder) {
        String selection;
        String[] selectionArgs;

        selection=MoviesContract.FavMovies.TABLE_NAME+
                "." + MoviesContract.FavMovies.COLUMN_MOVIE_ID + " = ? ";
        selectionArgs=new String[]{MoviesContract.FavMovies.getMovieIdFromUri(uri)};

        return moviesDbHelper.getReadableDatabase().query(
                MoviesContract.FavMovies.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match=sUriMatcher.match(uri);

        switch (match){
            case MOVIES:{
                return MoviesContract.FavMovies.CONTENT_TYPE;
            }
            case MOVIES_WITH_IDS: {
                return MoviesContract.FavMovies.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Uri Not Found "+uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db=moviesDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES: {
                long _id=db.insert(MoviesContract.FavMovies.TABLE_NAME,null,values);
                if( _id > 0 ){
                    returnUri=MoviesContract.FavMovies.buildMovieUri(_id);
                }
                else {
                    throw new SQLException("Failed to insert row into "+uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Uri not found "+uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db=moviesDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);

        switch (match){
            case MOVIES:
                db.beginTransaction();
                int returnCount=0;
                try {
                    for (ContentValues contentValues:values){
                        long _id=db.insert(MoviesContract.FavMovies.TABLE_NAME,null,contentValues);
                        if(_id!=-1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    getContext().getContentResolver().notifyChange(uri,null);
                    return returnCount;
                }
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db=moviesDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        int rowsDeleted;

        if(null==selection)
            selection="1";
        switch (match){
            case MOVIES: {
                rowsDeleted=db.delete(MoviesContract.FavMovies.TABLE_NAME,selection,selectionArgs);
                break;
            }
            default:{
                throw new UnsupportedOperationException("Uri not found "+uri);
            }
        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db=moviesDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case MOVIES: {
                rowsUpdated=db.update(MoviesContract.FavMovies.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            default:{
                throw new UnsupportedOperationException("Uri not found"+uri);
            }
        }
        if(rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}
