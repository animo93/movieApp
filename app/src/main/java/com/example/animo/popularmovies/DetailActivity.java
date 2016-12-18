package com.example.animo.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState==null){
            Intent intent = getIntent();
            MovieData movieData=intent.getParcelableExtra("extra_text");
            Log.e("DetailActivity","Name is "+movieData.movieName);
            Bundle args=new Bundle();
            args.putParcelable("extra_text",movieData);
            DetailActivityFragment detailActivityFragment=new DetailActivityFragment();
            detailActivityFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container,detailActivityFragment)
                    .commit();
        }

    }
}
