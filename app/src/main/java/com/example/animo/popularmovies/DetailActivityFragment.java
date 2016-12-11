package com.example.animo.popularmovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.animo.popularmovies.data.MoviesContract;
import com.example.animo.popularmovies.data.MoviesProvider;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String Log_tag=DetailActivityFragment.class.getSimpleName();
    ImageView imageView;
    TextView dateTextView;
    TextView timeTextView;
    TextView ratingTextView;
    TextView overviewTextView;
    String[] trailers;
    View rootView;
    ViewGroup container;
    Button button;

    Uri mUri;

    private static final int MOVIE_DETAIL_LOADER=0;

    private static final String[] MOVIE_DETAIL_COLUMNS={
            MoviesContract.FavMovies.TABLE_NAME+ "." + MoviesContract.FavMovies._ID,
            MoviesContract.FavMovies.COLUMN_DATE,
            MoviesContract.FavMovies.COLUMN_TIME,
            MoviesContract.FavMovies.COLUMN_RATING,
            MoviesContract.FavMovies.COLUMN_OVERVIEW,
            MoviesContract.FavMovies.COLUMN_BACKDROP_PATH
    };


    static final int COL_ID=0;
    static final int COL_MOVIE_DATE=1;
    static final int COL_MOVIE_TIME=2;
    static final int COL_MOVIE_RATING=3;
    static final int COL_MOVIE_OVERVIEW=4;
    static final int COL_MOVIE_BACKDROP_PATH=5;


    public DetailActivityFragment() {
    }

    public void onSortChanged() {
        getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER,null,this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(DetailActivityFragment.class.getSimpleName(), "inside onCreateView");
        MovieData movieData=null;
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        this.container= (ViewGroup) rootView.findViewById(R.id.traler_layout);

        imageView = (ImageView) rootView.findViewById(R.id.movie_image);
        dateTextView = (TextView) rootView.findViewById(R.id.movie_date);
        timeTextView = (TextView) rootView.findViewById(R.id.movie_time);
        ratingTextView = (TextView) rootView.findViewById(R.id.movie_rating);
        overviewTextView = (TextView) rootView.findViewById(R.id.movie_overview);
        button= (Button) rootView.findViewById(R.id.favorite);



        DetailViewTask detailViewTask = new DetailViewTask(this);
        Bundle arguments=getArguments();

        if(arguments!=null){
            movieData=arguments.getParcelable("extra_text");
        }
        //MovieData movieData=intent.getParcelableExtra("extra_text");
        if(movieData.movieId!=null){
            Log.d("DetailActivityFragment","is "+movieData.movieId);

            String sortPreference=Utility.getPreferredSortOrder(getContext());
            if(sortPreference.equals("favourite")){
                Log.d(Log_tag,"preference sort order is "+sortPreference);
                mUri= MoviesContract.FavMovies.buildMovieUri(Long.parseLong(movieData.movieId));
                getLoaderManager().initLoader(MOVIE_DETAIL_LOADER,null,this);
            } else {
                detailViewTask.execute(""+movieData.movieId);
                ViewTrailerTask viewTrailerTask=new ViewTrailerTask(this);
                viewTrailerTask.execute(""+movieData.movieId);

            }
        }



        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri!=null){
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_DETAIL_COLUMNS,
                    null,
                    null,
                    null

            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(Log_tag,"no of columns "+data.getColumnCount());
        if(data!=null && data.moveToFirst()){
            dateTextView.setText(data.getString(COL_MOVIE_DATE));
            timeTextView.setText(data.getString(COL_MOVIE_TIME));
            ratingTextView.setText(data.getString(COL_MOVIE_RATING));
            overviewTextView.setText(data.getString(COL_MOVIE_OVERVIEW));
            File image= new File(data.getString(COL_MOVIE_BACKDROP_PATH));
            Picasso.with(getContext())
                    .load(image)
                    .into(imageView);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
