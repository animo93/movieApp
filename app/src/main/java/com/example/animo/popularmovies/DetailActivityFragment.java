package com.example.animo.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    TextView titleTextView;
    ImageView imageView;
    TextView dateTextView;
    TextView timeTextView;
    TextView ratingTextView;
    TextView overviewTextView;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(DetailActivityFragment.class.getSimpleName(),"inside onCreateView");
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent=getActivity().getIntent();
        titleTextView= (TextView) rootView.findViewById(R.id.movie_title);
        imageView=(ImageView) rootView.findViewById(R.id.movie_image);
        dateTextView= (TextView) rootView.findViewById(R.id.movie_date);
        timeTextView= (TextView) rootView.findViewById(R.id.movie_time);
        ratingTextView= (TextView) rootView.findViewById(R.id.movie_rating);
        overviewTextView= (TextView) rootView.findViewById(R.id.movie_overview);
        DetailViewTask detailViewTask=new DetailViewTask();
        detailViewTask.execute(intent.getStringExtra(Intent.EXTRA_TEXT));
        return rootView;
    }
    public class DetailViewTask extends AsyncTask<String,Void,Object[]>{
        private final String LOG_TAG=DetailViewTask.class.getSimpleName();

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);
            Log.e(DetailActivityFragment.class.getSimpleName(), "inside onPostExecute");
            Log.e(DetailActivity.class.getSimpleName(), "movie name " + objects[0]);
            titleTextView.setText((String) objects[0]);
            Picasso.with(getContext())
                    .load((String) objects[1])
                    .fit()
                    .into(imageView);
            dateTextView.setText((String) objects[2]);
            timeTextView.setText((String) objects[3]);
            ratingTextView.setText((String) objects[4]);
            overviewTextView.setText((CharSequence) objects[5]);
        }

        private Object[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
            Log.e(DetailActivityFragment.class.getSimpleName(),"inside getMovieDataFromJson");
            JSONObject movieJson=new JSONObject(movieJsonStr);
            Object[] objects=new Object[6];
            String movieTitle;
            String moviePosterUrl;
            String movieReleaseDate;
            String movieTime;
            String movieRating;
            Object movieOverview;

            movieTitle=movieJson.getString("title");
            moviePosterUrl="http://image.tmdb.org/t/p/w185//"+movieJson.getString("backdrop_path");
            movieReleaseDate=movieJson.getString("release_date").substring(0,4);
            movieTime=movieJson.getString("runtime")+"min";
            movieRating=movieJson.getString("vote_average")+"/10";
            movieOverview= movieJson.get("overview");
            objects[0]=movieTitle;
            objects[1]=moviePosterUrl;
            objects[2]=movieReleaseDate;
            objects[3]=movieTime;
            objects[4]=movieRating;
            objects[5]=movieOverview;
            return objects;

        }


        @Override
        protected Object[] doInBackground(String... params) {
            HttpURLConnection httpURLConnection=null;
            BufferedReader reader=null;
            String appKey="10577495563a92834bd8503886bbcc5a";
            String movieDetailJson=null;
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0]+"?api_key=";
                final String APPID_PARAM = "api_key";

                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, appKey)
                        .build();
                URL url = new URL(buildUri.toString());
                Log.e(DetailActivityFragment.class.getSimpleName(),"url is"+url);

                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream=httpURLConnection.getInputStream();
                StringBuffer stringBuffer=new StringBuffer();
                if(inputStream==null)
                    movieDetailJson=null;

                reader=new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line=reader.readLine())!=null){
                    stringBuffer.append(line+"\n");
                }
                if(stringBuffer.length()==0)
                    movieDetailJson=null;

                movieDetailJson=stringBuffer.toString();


            } catch (IOException e) {
                Log.e("MainActivityFragment", "error", e);
                movieDetailJson=null;

            } finally {
                if(httpURLConnection!=null)
                    httpURLConnection.disconnect();
                if(reader!=null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("MainActivityFragment","error",e);
                    }
                }
            }
            try{
                return getMovieDataFromJson(movieDetailJson);
            } catch (JSONException e) {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }
    }
}
