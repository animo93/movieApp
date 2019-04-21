package com.example.animo.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by animo on 23/10/16.
 */
public class FetchSongsTask extends AsyncTask<String, Void, List<MovieData>> {
    private MainActivityFragment mainActivityFragment;
    private final String LOG_TAG = FetchSongsTask.class.getSimpleName();
    private final Context mContext;
    MovieAdapter movieAdapter;


    public FetchSongsTask(Context context){
        mContext=context;
    }

    public FetchSongsTask(MainActivityFragment mainActivityFragment) {
        this.mainActivityFragment = mainActivityFragment;
        mContext = null;
    }

    @Override
    protected void onPostExecute(List<MovieData> movieDataList) {
        super.onPostExecute(movieDataList);
        if(movieDataList.size()==0){
            mainActivityFragment.listView.setAdapter(movieAdapter);
        }
        else {
            mainActivityFragment.imageListAdapter = new ImageListAdapter(mainActivityFragment.getContext(), movieDataList);
            mainActivityFragment.listView.setAdapter(mainActivityFragment.imageListAdapter);
            mainActivityFragment.imageListAdapter.add(movieDataList);
        }

    }



    private List<MovieData> getSongDataFromJson(String songJsonStr) throws JSONException {
        JSONObject songJson = new JSONObject(songJsonStr);
        JSONArray songArray = songJson.names();
        String[] results = new String[songArray.length()];


        for (int i = 0; i < songArray.length(); i++) {
            String imageUrl;
            JSONObject songDetails = songArray.getJSONObject(i);
            imageUrl = songDetails.getString("cover_image");
            results[i] = imageUrl;
            mainActivityFragment.songName = songDetails.getString("song");
            mainActivityFragment.artists = songDetails.getString("artists");

        }
        return results;

    }

    @Override
    protected List<MovieData> doInBackground(String... params) {
        Log.e(LOG_TAG, "inside do in background");
        if(params.length==0){
            return null;
        }
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String songJson = null;


        try {
            final String BASE_URL = "http://starlord.hackerearth.com/edfora/cokestudio";

            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .build();
            URL url = new URL(buildUri.toString());
            Log.e(LOG_TAG, "url is " + url);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null)
                songJson = null;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            if (stringBuffer.length() == 0)
                songJson = null;

            songJson = stringBuffer.toString();


        } catch (IOException e) {
            Log.e("MainActivityFragment", "error", e);
            songJson = null;

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
            return getSongDataFromJson(songJson);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        return null;
    }


}
