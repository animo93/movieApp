package com.example.animo.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
 * Created by animo on 17/12/16.
 */

public class ReadReviewTask extends AsyncTask<String, Void, String[]>{
    private final String Log_tag=ReadReviewTask.class.getSimpleName();
    private DetailActivityFragment detailActivityFragment;

    public ReadReviewTask(DetailActivityFragment detailActivityFragment){
        this.detailActivityFragment=detailActivityFragment;
    }

    @Override
    protected String[] doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;
            String appKey = "10577495563a92834bd8503886bbcc5a";
            String movieReviewJson = null;
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?api_key=";
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
                    movieReviewJson = null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer.length() == 0)
                    movieReviewJson = null;

                movieReviewJson = stringBuffer.toString();


            } catch (IOException e) {
                Log.e(Log_tag, "error", e);
                movieReviewJson = null;

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
                return getReviewsFromJson(movieReviewJson);
            } catch (Exception e) {
                Log.e(Log_tag, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

    private String[] getReviewsFromJson(String movieReviewJson) throws JSONException {
        Log.e(Log_tag,movieReviewJson);

        JSONObject jsonObject=new JSONObject(movieReviewJson);
        JSONArray reviewArray=jsonObject.getJSONArray("results");
        String[] reviews=new String[reviewArray.length()];
        Log.e(Log_tag,""+reviewArray.length());
        for(int i=0;i<reviewArray.length();i++){
            JSONObject trailerObject=reviewArray.getJSONObject(i);
            String url=trailerObject.getString("url");
            reviews[i]=url;
        }
        return reviews;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        detailActivityFragment.reviews=strings;
        int counter=1;
        for (final String reviewUrl:strings){
            View movieReview= LayoutInflater.from(detailActivityFragment.getContext()).inflate(
                    R.layout.review_item,
                    null
            );
            TextView textView= (TextView) movieReview.findViewById(R.id.reviews);
            textView.setText("Review "+counter);
            counter++;
            movieReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent readReview=new Intent(Intent.ACTION_VIEW,Uri.parse(reviewUrl));
                    detailActivityFragment.startActivity(readReview);
                }
            });
            this.detailActivityFragment.reviewContainer.addView(movieReview);

        }

    }
}
