package com.example.animo.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

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
    TextView titleTextView;
    ImageView imageView;
    TextView dateTextView;
    TextView timeTextView;
    TextView ratingTextView;
    TextView overviewTextView;
    ScrollView overviewScrollView;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(DetailActivityFragment.class.getSimpleName(), "inside onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        titleTextView = (TextView) rootView.findViewById(R.id.movie_title);
        imageView = (ImageView) rootView.findViewById(R.id.movie_image);
        dateTextView = (TextView) rootView.findViewById(R.id.movie_date);
        timeTextView = (TextView) rootView.findViewById(R.id.movie_time);
        ratingTextView = (TextView) rootView.findViewById(R.id.movie_rating);
        overviewScrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        overviewTextView = (TextView) rootView.findViewById(R.id.movie_overview);
        DetailViewTask detailViewTask = new DetailViewTask(this);
        MovieData movieData=intent.getParcelableExtra("extra_text");
        //detailViewTask.execute(intent.getStringExtra(Intent.EXTRA_TEXT));
        Log.e("DetailActivityFragment","is "+movieData.movieId);
        detailViewTask.execute(""+movieData.movieId);
        return rootView;
    }

    private String[] getMovieTrailer(String... params){
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String appKey = "10577495563a92834bd8503886bbcc5a";
        String movieTrailerJson = null;
        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?api_key=";
            final String APPID_PARAM = "api_key";

            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, appKey)
                    .build();
            URL url = new URL(buildUri.toString());
            Log.e(Log_tag, "url is" + url);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null)
                movieTrailerJson = null;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            if (stringBuffer.length() == 0)
                movieTrailerJson = null;

            movieTrailerJson = stringBuffer.toString();


        } catch (IOException e) {
            Log.e(Log_tag, "error", e);
            movieTrailerJson = null;

        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(Log_tag, "error", e);
                }
            }
        }
        try {
            return getMovieTrailerFromJson(movieTrailerJson);
        } catch (Exception e) {
            Log.e(Log_tag, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private String[] getMovieTrailerFromJson(String movieDetailJson) throws JSONException {
        final String trailerBaseUrl="https://www.youtube.com/watch?v=";
        String[] trailers=new String[]{};
        JSONObject jsonObject=new JSONObject(movieDetailJson);
        JSONArray trailerArray=jsonObject.getJSONArray("results");
        for(int i=0;i<trailerArray.length();i++){
            JSONObject trailerObject=trailerArray.getJSONObject(i);
            String key=trailerObject.getString("key");
            Uri buildUri = Uri.parse(trailerBaseUrl).buildUpon()
                    .appendQueryParameter("v", key)
                    .build();
            String trailerUrl=buildUri.toString();
            trailers[i]=trailerUrl;
        }
        return trailers;
    }
}
