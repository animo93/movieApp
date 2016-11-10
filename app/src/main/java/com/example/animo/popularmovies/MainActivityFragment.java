package com.example.animo.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.animo.popularmovies.data.MoviesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ImageListAdapter imageListAdapter;
    MovieAdapter movieAdapter;
    GridView gridView;
    String[] movieIds;
    String[] movieNames;
    MovieData movieData;

    private static final int MOVIE_LOADER=0;

    static final int COL_MOVIE_ID=0;
    static final int COL_MOVIE_TITLE=1;
    static final int COL_MOVIE_DATE=2;
    static final int COL_MOVIE_TIME=3;
    static final int COL_MOVIE_RATING=4;
    static final int COL_MOVIE_OVERVIEW=5;
    static final int COL_MOVIE_POSTER_PATH=6;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.e("inside NetworkAvailable", "activeNetworkInfo is " + activeNetworkInfo);
        return null != activeNetworkInfo && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //getLoaderManager().initLoader(MOVIE_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(this);
        if (isNetworkAvailable()) {
            String preferredSortOrder=Utility.getPreferredSortOrder(getActivity());
            Log.v("inside onCreateView ","preferedSortOrder is "+preferredSortOrder);
            fetchMoviesTask.execute(Utility.getPreferredSortOrder(getActivity()));
            //movieAdapter=new MovieAdapter(getActivity(),null,0);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    long details = imageListAdapter.getItemId(position);
                    movieData = new MovieData(movieIds[((int) details)],movieNames[(int) details]);
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("extra_text", movieData);
                    startActivity(intent);
                }
            });
        }

        return rootView;
    }
/*

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri moviePosterUri= MoviesContract.FavMovies.CONTENT_URI;

        String[] projection={MoviesContract.FavMovies.COLUMN_POSTER_PATH};
        //Cursor cursor=getActivity().getContentResolver().query(moviePosterUri,projection,null,null,null);
        return new CursorLoader(getActivity(),
                moviePosterUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);

    }*/
}
