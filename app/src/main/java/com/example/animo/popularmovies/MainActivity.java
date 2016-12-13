package com.example.animo.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private String sortOption;
    private Boolean mTwoPane;
    private static final String DETAIlFRAGMENT_TAG="DFTAG";

    @Override
    protected void onResume() {
        Log.e("MainActivity","inside onResume");
        String sortOption=Utility.getPreferredSortOrder(this);
        Log.e("MainActivity","sort order "+sortOption);
        if(sortOption!=null && !sortOption.equals(this.sortOption)){
            Log.e("MainActivity","inside if");
            MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if(mainActivityFragment!=null){
                mainActivityFragment.onOptionsChanged();
            }
            DetailActivityFragment detailActivityFragment = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAIlFRAGMENT_TAG);
            if(detailActivityFragment!=null){
                detailActivityFragment.onSortChanged();
            }

        }
        this.sortOption=sortOption;
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("MainActivity","inside onCreate");
        sortOption=Utility.getPreferredSortOrder(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pop Movies");
        setSupportActionBar(toolbar);

        if(findViewById(R.id.movie_detail_container)!=null){
            Log.e("MainActivity","mTwoPane is true");
            mTwoPane=true;
            if(savedInstanceState==null){
                Log.e("MainActivity","savedInstanceState is null");
                DetailActivityFragment detailActivityFragment=new DetailActivityFragment();
                MovieData movieData=new MovieData(null,null,"true");

                Bundle bundle=new Bundle();
                bundle.putParcelable("extra_text",movieData);

                detailActivityFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container,detailActivityFragment,DETAIlFRAGMENT_TAG)
                        .commit();
            }
        }else {
            mTwoPane=false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String movieId,String movieTitle) {
        Log.e("MainActivity","inside onItemSelected mTwoPane"+mTwoPane);
        String mTwoPane=(this.mTwoPane)?"true":"false";
        MovieData movieData=new MovieData(movieId,movieTitle,mTwoPane);
        if(this.mTwoPane){
            Bundle args=new Bundle();
            args.putParcelable("extra_text",movieData);

            DetailActivityFragment detailActivityFragment=new DetailActivityFragment();
            detailActivityFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container,detailActivityFragment,DETAIlFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this,DetailActivity.class).putExtra("extra_text",movieData);
            startActivity(intent);
        }

    }

    @Override
    public void changemovieTitle(String title) {

        /*CollapsingToolbarLayout collapsingToolbarLayout=
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_two_pane);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.TextAppearance_Movies_Title_Collapsed);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.TextAppearance_Movies_Title_Expanded);
        collapsingToolbarLayout.setTitle(title);*/
    }
}
