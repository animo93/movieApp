package com.example.animo.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

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
 * Created by animo on 11/11/16.
 */

public class ViewTrailerTask extends AsyncTask<String, Void, String[]> {

    private final String Log_tag=ViewTrailerTask.class.getSimpleName();

    private DetailActivityFragment detailActivityFragment;

    public ViewTrailerTask(DetailActivityFragment detailActivityFragment){
        this.detailActivityFragment=detailActivityFragment;
    }

    @Override
    protected String[] doInBackground(String... params) {
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
        Log.e(Log_tag,movieDetailJson);
        final String trailerBaseUrl="https://www.youtube.com/watch?v=";

        JSONObject jsonObject=new JSONObject(movieDetailJson);
        JSONArray trailerArray=jsonObject.getJSONArray("results");
        String[] trailers=new String[trailerArray.length()];
        Log.e(Log_tag,""+trailerArray.length());
        for(int i=0;i<trailerArray.length();i++){
            JSONObject trailerObject=trailerArray.getJSONObject(i);
            String key=trailerObject.getString("key");
            Uri buildUri = Uri.parse(trailerBaseUrl).buildUpon()
                    .appendQueryParameter("v", key)
                    .build();

            String trailerUrl=buildUri.toString();
            Log.e(Log_tag,trailerUrl);
            trailers[i]=trailerUrl;
        }
        return trailers;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        detailActivityFragment.trailers=strings;
        for(final String trailer:strings){
            View movieTrailer= LayoutInflater.from(detailActivityFragment.getContext()).inflate(
                    R.layout.trailer_item,null
            );
            movieTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playYouTubeTrailerIntent(trailer);
                }
            });
            detailActivityFragment.container.addView(movieTrailer);

        }

    }

    private void playYouTubeTrailerIntent(String trailer) {
        Intent trailerPlay=new Intent(Intent.ACTION_VIEW);
        trailerPlay.setDataAndType(Uri.parse(trailer),"video/*");
        detailActivityFragment.startActivity(trailerPlay);
    }
}
