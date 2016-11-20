package com.example.animo.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

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
        Log.e(DetailActivityFragment.class.getSimpleName(), "inside onPostExecute");
        Log.e(DetailActivity.class.getSimpleName(), "movie name " + objects[0]);
        String title = (String) objects[0];
        Log.e(LOG_TAG, "Title is " + title);
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
                Picasso.with(detailActivityFragment.getContext())
                        .load((String) objects[1])
                        .into(target((String) objects[0]));
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.d(LOG_TAG, String.format("Message", msg));
                        switch (msg.what) {
                            case TASK_COMPLETE:
                                String path = (String) msg.obj;
                                Log.d(LOG_TAG,"The File path is "+path);
                                filePath = path;
                                break;
                            default:
                                super.handleMessage(msg);

                        }

                    }
                };
            }
        });

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

    private Target target(final String imageName) {
        verifyStoragePermissions(detailActivityFragment.getActivity());
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(isExternalStorageWritable()){
                            /*file = new File(
                                    Environment.getExternalStorageDirectory().getPath()
                                            + "/" + imageName + ".jpg");*/
                            file=getAlbumStorageDir(detailActivityFragment.getContext(),"movieApp");
                            try {
                                file.createNewFile();
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
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

    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }




}
