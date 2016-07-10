package com.example.animo.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ImageListAdapter imageListAdapter;
    GridView gridView;
    String []movieIds;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_sort){
            Log.e(MainActivityFragment.class.getSimpleName(),"inside menu");
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.e(MainActivity.class.getSimpleName(), "inside oncreate");


    }

    public MainActivityFragment() {
    }

    public class ImageListAdapter extends BaseAdapter {
        private Context context;
        private String[] imageUrls;
        public ImageListAdapter(Context context,String[] imageUrls){
            super();
            this.context=context;
            this.imageUrls=imageUrls;


        }

        public void add(String[] strings){
            this.imageUrls=strings;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return imageUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return imageUrls[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //GridView gridView;
            View view;
            if(convertView==null){
                //gridView= (GridView) inflater.inflate(R.layout.fragment_main,null);
                view=inflater.inflate(R.layout.movie_list,parent,false);
                ImageView imageView= (ImageView) view.findViewById(R.id.imageView);
                Picasso
                        .with(context)
                        .load(imageUrls[position])
                        .into(imageView);
            }
            else {
                view= convertView;
            }



            return view;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(MainActivityFragment.class.getSimpleName(),"inside onStart");
        /*FetchMoviesTask fetchMoviesTask=new FetchMoviesTask();
        fetchMoviesTask.execute();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_main, container, false);
        gridView= (GridView) rootView.findViewById(R.id.gridView);
        FetchMoviesTask fetchMoviesTask=new FetchMoviesTask();
        fetchMoviesTask.execute();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long details=imageListAdapter.getItemId(position);
                Intent intent=new Intent(getActivity(),DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,movieIds[((int) details)]);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<Void,Void,String[]>{
        private final String LOG_TAG=FetchMoviesTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            imageListAdapter=new ImageListAdapter(getContext(),strings);
            gridView.setAdapter(imageListAdapter);

            imageListAdapter.add(strings);



        }

        private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
            JSONObject movieJson=new JSONObject(movieJsonStr);
            JSONArray movieArray=movieJson.getJSONArray("results");
            String[] results=new String[movieArray.length()];
            movieIds=new String[movieArray.length()];


            for(int i=0;i<movieArray.length();i++){
                String posterUrl;
                JSONObject movieDetails=movieArray.getJSONObject(i);
                posterUrl=movieDetails.getString("poster_path");
                results[i]="http://image.tmdb.org/t/p/w185//"+posterUrl;
                movieIds[i]=movieDetails.getString("id");
            }
            return results;

        }

        @Override
        protected String[] doInBackground(Void... params) {
            Log.e(LOG_TAG,"inside do in background");
            HttpURLConnection httpURLConnection=null;
            BufferedReader reader=null;
            String appKey="10577495563a92834bd8503886bbcc5a";
            String movieJson=null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=";
                final String APPID_PARAM = "api_key";

                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, appKey)
                        .build();
                URL url = new URL(buildUri.toString());

                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream=httpURLConnection.getInputStream();
                StringBuffer stringBuffer=new StringBuffer();
                if(inputStream==null)
                    movieJson=null;

                reader=new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line=reader.readLine())!=null){
                    stringBuffer.append(line+"\n");
                }
                if(stringBuffer.length()==0)
                    movieJson=null;

                movieJson=stringBuffer.toString();


            } catch (IOException e) {
                Log.e("MainActivityFragment","error",e);
                movieJson=null;

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
                return getMovieDataFromJson(movieJson);
            } catch (JSONException e) {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }
    }
}
