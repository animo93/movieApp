package com.example.animo.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.animo.popularmovies.data.MoviesContract;

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
 * Created by animo on 23/10/16.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
    private MainActivityFragment mainActivityFragment;
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;
    MovieAdapter movieAdapter;


    public FetchMoviesTask(Context context){
        mContext=context;
    }

    public FetchMoviesTask(MainActivityFragment mainActivityFragment) {
        this.mainActivityFragment = mainActivityFragment;
        mContext = null;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        if(strings==null){
            mainActivityFragment.gridView.setAdapter(movieAdapter);
        }
        else {
            mainActivityFragment.imageListAdapter = new ImageListAdapter(mainActivityFragment.getContext(), strings);
            mainActivityFragment.gridView.setAdapter(mainActivityFragment.imageListAdapter);
            mainActivityFragment.imageListAdapter.add(strings);
        }

    }



    private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");
        String[] results = new String[movieArray.length()];
        mainActivityFragment.movieIds = new String[movieArray.length()];
        mainActivityFragment.movieNames = new String[movieArray.length()];


        for (int i = 0; i < movieArray.length(); i++) {
            String posterUrl;
            JSONObject movieDetails = movieArray.getJSONObject(i);
            posterUrl = movieDetails.getString("poster_path");
            results[i] = "http://image.tmdb.org/t/p/w185//" + posterUrl;
            mainActivityFragment.movieIds[i] = movieDetails.getString("id");
            mainActivityFragment.movieNames[i] = movieDetails.getString("original_title");
        }
        return results;

    }

    @Override
    protected String[] doInBackground(String... params) {
        Log.e(LOG_TAG, "inside do in background");
        if(params.length==0){
            return null;
        }

        String SORT_ORDER=params[0];
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String appKey = "10577495563a92834bd8503886bbcc5a";
        String movieJson = null;


        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + SORT_ORDER + "?";
            final String APPID_PARAM = "api_key";

            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, appKey)
                    .build();
            URL url = new URL(buildUri.toString());
            Log.e(LOG_TAG, "url is " + url);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null)
                movieJson = null;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            if (stringBuffer.length() == 0)
                movieJson = null;

            movieJson = stringBuffer.toString();


        } catch (IOException e) {
            Log.e("MainActivityFragment", "error", e);
            movieJson = null;

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
            return getMovieDataFromJson(movieJson);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        return null;
    }


}
