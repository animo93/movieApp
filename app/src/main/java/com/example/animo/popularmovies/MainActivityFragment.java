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
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG=MainActivityFragment.class.getSimpleName();

    ImageListAdapter imageListAdapter;
    MovieAdapter movieAdapter;
    GridView gridView;
    String[] movieIds;
    String[] movieNames;
    MovieData movieData;

    private static final int MOVIE_LOADER=0;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.FavMovies.TABLE_NAME+ "." + MoviesContract.FavMovies._ID,
            MoviesContract.FavMovies.COLUMN_TITLE ,
            MoviesContract.FavMovies.COLUMN_POSTER_PATH,
            MoviesContract.FavMovies.COLUMN_MOVIE_ID
    };

    static final int COL_ID=0;
    static final int COL_MOVIE_TITLE=1;
    static final int COL_MOVIE_POSTER_PATH=2;
    static final int COL_MOVIE_ID=3;

    public interface Callback {
        public void onItemSelected(String movieId,String movieTitle);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.e("inside NetworkAvailable", "activeNetworkInfo is " + activeNetworkInfo);
        return null != activeNetworkInfo && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        String preferredSortOrder=Utility.getPreferredSortOrder(getActivity());
        if(preferredSortOrder.equals("favourite")){
            getLoaderManager().initLoader(MOVIE_LOADER,null,this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("inside onOptionsItem","item is"+item.getGroupId());
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
    public void onResume() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(this);
        if (isNetworkAvailable()) {
            final String preferredSortOrder=Utility.getPreferredSortOrder(getActivity());
            Log.v("inside onResume ","preferedSortOrder is "+preferredSortOrder);
            if(preferredSortOrder.equals("favourite")){
                Log.e(LOG_TAG,"inside if "+preferredSortOrder);
                getLoaderManager().restartLoader(MOVIE_LOADER,null,this);
                movieAdapter=new MovieAdapter(getActivity(),null,0);
                gridView.setAdapter(movieAdapter);
            }
            else {
                fetchMoviesTask.execute(preferredSortOrder);
            }

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(preferredSortOrder.equals("favourite")){
                        Cursor cursor= (Cursor) parent.getItemAtPosition(position);
                        if(cursor!=null){
                            ((Callback)getActivity())
                                    .onItemSelected(cursor
                                    .getString(COL_MOVIE_ID),cursor.getString(COL_MOVIE_TITLE));
                        }
                    } else {
                        long details = imageListAdapter.getItemId(position);
                        movieData = new MovieData(movieIds[((int) details)],movieNames[(int) details]);
                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra("extra_text", movieData);
                        startActivity(intent);
                    }


                }
            });
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String preferredSortOrder=Utility.getPreferredSortOrder(getActivity());
        Log.e(LOG_TAG,"inside onCreateLoader and preferredSortOrder "+preferredSortOrder);
        if(preferredSortOrder.equals("favourite")){
            return new CursorLoader(getActivity(),
                    MoviesContract.FavMovies.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG,"data count is "+data.getCount());
        movieAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}
