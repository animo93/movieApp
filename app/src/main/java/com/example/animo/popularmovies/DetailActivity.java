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

public class DetailActivity extends AppCompatActivity implements DetailActivityFragment.DetailCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState==null){
            /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/



            /*CollapsingToolbarLayout collapsingToolbarLayout=
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_two_pane);*/
            Intent intent = getIntent();
            MovieData movieData=intent.getParcelableExtra("extra_text");
            Log.e("DetailActivity","Name is "+movieData.movieName);
            /*collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.TextAppearance_Movies_Title_Collapsed);
            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.TextAppearance_Movies_Title_Expanded);
            collapsingToolbarLayout.setTitle(movieData.movieName);*/

            Bundle args=new Bundle();
            args.putParcelable("extra_text",movieData);
            DetailActivityFragment detailActivityFragment=new DetailActivityFragment();
            detailActivityFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container,detailActivityFragment)
                    .commit();
        }





    }

    @Override
    public void changemovieTitle(String title) {
        CollapsingToolbarLayout collapsingToolbarLayout=
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_two_pane);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.TextAppearance_Movies_Title_Collapsed);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.TextAppearance_Movies_Title_Expanded);
        collapsingToolbarLayout.setTitle(title);
    }
}
