package com.example.animo.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    @Override
    protected void onResume() {
        Log.e("MainActivity","inside onResume");
        String sortOption=Utility.getPreferredSortOrder(this);
        Log.e("MainActivity","sort order "+sortOption);
        if(sortOption!=null && !sortOption.equals(this.sortOption)){
            Log.e("MainActivity","inside if");
            MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            if(mainActivityFragment!=null){
                mainActivityFragment.onOptionsChanged();
            }
            DetailActivityFragment detailActivityFragment = (DetailActivityFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
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
        MovieData movieData=new MovieData(movieId,movieTitle);
        Intent intent = new Intent(this,DetailActivity.class).putExtra("extra_text",movieData);
        startActivity(intent);
    }
}
