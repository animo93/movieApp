package com.example.animo.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by animo on 25/10/16.
 */
public class DetailViewTask extends AsyncTask<String, Void, Object[]> {
    private DetailActivityFragment detailActivityFragment;
    private final String LOG_TAG = DetailViewTask.class.getSimpleName();

    public DetailViewTask(DetailActivityFragment detailActivityFragment) {
        this.detailActivityFragment = detailActivityFragment;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        super.onPostExecute(objects);
        Log.e(DetailActivityFragment.class.getSimpleName(), "inside onPostExecute");
        Log.e(DetailActivity.class.getSimpleName(), "movie name " + objects[0]);
        detailActivityFragment.titleTextView.setText((String) objects[0]);
        Picasso.with(detailActivityFragment.getContext())
                .load((String) objects[1])
                .fit()
                .into(detailActivityFragment.imageView);
        detailActivityFragment.dateTextView.setText((String) objects[2]);
        detailActivityFragment.timeTextView.setText((String) objects[3]);
        detailActivityFragment.ratingTextView.setText((String) objects[4]);
        detailActivityFragment.overviewTextView.setText((CharSequence) objects[5]);

    }

    private Object[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
        Log.e(DetailActivityFragment.class.getSimpleName(), "inside getMovieDataFromJson");
        JSONObject movieJson = new JSONObject(movieJsonStr);
        Object[] objects = new Object[6];
        String movieTitle;
        String moviePosterUrl;
        String movieReleaseDate;
        String movieTime;
        String movieRating;
        Object movieOverview;

        movieTitle = movieJson.getString("title");
        moviePosterUrl = "http://image.tmdb.org/t/p/w185//" + movieJson.getString("backdrop_path");
        movieReleaseDate = movieJson.getString("release_date").substring(0, 4);
        movieTime = movieJson.getString("runtime") + "min";
        movieRating = movieJson.getString("vote_average") + "/10";
        movieOverview = movieJson.get("overview");
        objects[0] = movieTitle;
        objects[1] = moviePosterUrl;
        objects[2] = movieReleaseDate;
        objects[3] = movieTime;
        objects[4] = movieRating;
        objects[5] = movieOverview;
        return objects;

    }


    @Override
    protected Object[] doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String appKey = "10577495563a92834bd8503886bbcc5a";
        String movieDetailJson = null;
        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "?api_key=";
            final String APPID_PARAM = "api_key";

            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, appKey)
                    .build();
            URL url = new URL(buildUri.toString());
            Log.e(DetailActivityFragment.class.getSimpleName(), "url is" + url);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null)
                movieDetailJson = null;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            if (stringBuffer.length() == 0)
                movieDetailJson = null;

            movieDetailJson = stringBuffer.toString();


        } catch (IOException e) {
            Log.e("MainActivityFragment", "error", e);
            movieDetailJson = null;

        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("MainActivityFragment", "error", e);
                }
            }
        }
        try {
            return getMovieDataFromJson(movieDetailJson);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

}
