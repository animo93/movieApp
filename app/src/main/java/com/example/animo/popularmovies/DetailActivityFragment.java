package com.example.animo.popularmovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import com.example.animo.popularmovies.data.MoviesProvider;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
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


    public DetailActivityFragment() {
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(DetailActivityFragment.class.getSimpleName(), "inside onCreateView");
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
        MovieData movieData=intent.getParcelableExtra("extra_text");

        Log.e("DetailActivityFragment","is "+movieData.movieId);

        detailViewTask.execute(""+movieData.movieId);

        ViewTrailerTask viewTrailerTask=new ViewTrailerTask(this);
        viewTrailerTask.execute(""+movieData.movieId);

        return rootView;
    }

}
