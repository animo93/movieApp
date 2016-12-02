package com.example.animo.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.animo.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.jar.Manifest;

/**
 * Created by animo on 25/10/16.
 */
public class DetailViewTask extends AsyncTask<String, Void, Object[]> {
    private static final int REQUEST_EXTERNAL_STORAGE=1;
    private static String[] PERMISSIONS_STORAGE={
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private DetailActivityFragment detailActivityFragment;
    private File file;
    private String filePath;
    private Handler handler;

    private final String LOG_TAG = DetailViewTask.class.getSimpleName();
    private final int TASK_COMPLETE=0;

    public DetailViewTask(DetailActivityFragment detailActivityFragment) {
        this.detailActivityFragment = detailActivityFragment;
    }


    @Override
    protected void onPostExecute(final Object[] objects) {
        super.onPostExecute(objects);
        Log.d(LOG_TAG, "inside onPostExecute");
        final CharSequence text="Movie Marked as Favourite";

        Picasso.with(detailActivityFragment.getContext())
                .load((String) objects[1])
                .fit()
                .into(detailActivityFragment.imageView);
        detailActivityFragment.dateTextView.setText((String) objects[2]);
        detailActivityFragment.timeTextView.setText((String) objects[3]);
        detailActivityFragment.ratingTextView.setText((String) objects[4]);
        detailActivityFragment.overviewTextView.setText((CharSequence) objects[5]);
        detailActivityFragment.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Cursor movieCursor=detailActivityFragment.getContext().getContentResolver().query(
                        MoviesContract.FavMovies.buildMovieUriFromId((String) objects[7]),
                        new String[]{MoviesContract.FavMovies.TABLE_NAME+"."+MoviesContract.FavMovies._ID},
                        null,
                        null,
                        null
                );
                if(movieCursor.moveToFirst()){
                    String text="Movie is already marked favourite";
                    int duration=Toast.LENGTH_SHORT;
                    Toast toast=Toast.makeText(detailActivityFragment.getContext(),text,duration);
                    toast.show();
                    return;
                }
                verifyStoragePermissions(detailActivityFragment.getActivity());

                Picasso.with(detailActivityFragment.getContext())
                        .load((String) objects[6])
                        .into(target());

                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.d(LOG_TAG, String.format("Message", msg));
                        switch (msg.what) {
                            case TASK_COMPLETE:
                                String path = (String) msg.obj;
                                Log.d(LOG_TAG,"The File path is "+path);
                                filePath = path;
                                final ContentValues movieValues=new ContentValues();
                                movieValues.put(MoviesContract.FavMovies.COLUMN_TITLE, (String) objects[0]);
                                movieValues.put(MoviesContract.FavMovies.COLUMN_DATE, (String) objects[2]);
                                movieValues.put(MoviesContract.FavMovies.COLUMN_OVERVIEW, (String) objects[5]);
                                movieValues.put(MoviesContract.FavMovies.COLUMN_RATING, (String) objects[4]);
                                movieValues.put(MoviesContract.FavMovies.COLUMN_TIME, (String) objects[3]);
                                movieValues.put(MoviesContract.FavMovies.COLUMN_POSTER_PATH,filePath);
                                movieValues.put(MoviesContract.FavMovies.COLUMN_MOVIE_ID, (String) objects[7]);

                                Picasso.with(detailActivityFragment.getContext())
                                        .load((String) objects[1])
                                        .into(target());

                                handler = new Handler(Looper.getMainLooper()){
                                    @Override
                                    public void handleMessage(Message msg) {
                                        switch (msg.what) {
                                            case TASK_COMPLETE:
                                                String backdropPath= (String) msg.obj;
                                                Log.d(LOG_TAG,"Backdrop file path "+backdropPath);
                                                movieValues.put(MoviesContract.FavMovies.COLUMN_BACKDROP_PATH,backdropPath);
                                                detailActivityFragment.getContext().getContentResolver().insert(MoviesContract.FavMovies.CONTENT_URI,
                                                        movieValues);
                                                break;
                                            default:
                                                super.handleMessage(msg);
                                        }
                                    }
                                };
                                break;
                            default:
                                super.handleMessage(msg);

                        }
                        int duration= Toast.LENGTH_SHORT;
                        Toast toast=Toast.makeText(detailActivityFragment.getContext(),text,duration);
                        toast.show();

                    }
                };


            }


        });

    }


    private Object[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
        Log.e(DetailActivityFragment.class.getSimpleName(), "inside getMovieDataFromJson");
        JSONObject movieJson = new JSONObject(movieJsonStr);
        Object[] objects = new Object[8];
        String movieTitle;
        String movieId;
        String moviePosterUrl;
        String movieBackdropPath;
        String movieReleaseDate;
        String movieTime;
        String movieRating;
        Object movieOverview;

        movieTitle = movieJson.getString("title");
        movieId=movieJson.getString("id");
        movieBackdropPath = "http://image.tmdb.org/t/p/w185//" + movieJson.getString("backdrop_path");
        moviePosterUrl = "http://image.tmdb.org/t/p/w185//" + movieJson.getString("poster_path");
        movieReleaseDate = movieJson.getString("release_date").substring(0, 4);
        movieTime = movieJson.getString("runtime") + "min";
        movieRating = movieJson.getString("vote_average") + "/10";
        movieOverview = movieJson.get("overview");
        objects[0] = movieTitle;
        objects[1] = movieBackdropPath;
        objects[2] = movieReleaseDate;
        objects[3] = movieTime;
        objects[4] = movieRating;
        objects[5] = movieOverview;
        objects[6] = moviePosterUrl;
        objects[7] = movieId;

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

    private Target target() {
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(isExternalStorageWritable()){
                            file=getAlbumStorageDir(detailActivityFragment.getContext());
                            try {
                                file.createNewFile();
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                fileOutputStream.close();
                                Log.e(LOG_TAG, "File path is" + file.getAbsolutePath());
                                Message message=handler.obtainMessage(TASK_COMPLETE,file.getAbsolutePath());
                                message.sendToTarget();


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

    }

    private void verifyStoragePermissions(Activity activity) {
        int permission= ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(Context context) {
        String root=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File myDir=new File(root+"/saved_images");
        myDir.mkdirs();
        Random generator=new Random();
        int n=10000;
        n=generator.nextInt(n);
        String fname="Image-"+n+".png";
        File file=new File(myDir,fname);
        if(file.exists())
            file.delete();
        return file;
    }




}
