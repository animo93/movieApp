package com.example.animo.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ListView;

import com.example.animo.popularmovies.data.MoviesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String LOG_TAG=MainActivityFragment.class.getSimpleName();

    ImageListAdapter imageListAdapter;
    MovieAdapter movieAdapter;
    ListView listView;
    String[] movieIds;
    String[] movieNames;
    MovieData movieData;

    public interface Callback {
        public void onItemSelected(String movieId,String movieTitle);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d("inside NetworkAvailable", "activeNetworkInfo is " + activeNetworkInfo);
        return null != activeNetworkInfo && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(LOG_TAG,"inside onActivityCreated");
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
        Log.e(LOG_TAG,"inside onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    public void onOptionsChanged(){
        FetchSongsTask fetchSongsTask = new FetchSongsTask(this);

        if (isNetworkAvailable())  {
            fetchSongsTask.execute(preferredSortOrder);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String movieId=null;
                    String movieName=null;
                    long details= imageListAdapter.getItemId(position);
                    movieId = movieIds[(int) details];
                    movieName = movieNames[(int) details];
                    Log.e(LOG_TAG,"movieId is "+movieId+" and movieName is "+movieName);
                    ((Callback)getActivity())
                            .onItemSelected(movieId,movieName);


                }
            });
        }else {
            movieAdapter.swapCursor(null);
        }
    }

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(LOG_TAG,"inside onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        movieAdapter=new MovieAdapter(getActivity(),null,0);
        if(isNetworkAvailable())
            onOptionsChanged();
        return rootView;
    }

}
