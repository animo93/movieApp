package com.example.animo.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by animo on 27/10/16.
 */
public class MovieAdapter extends CursorAdapter{
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.fragment_main,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView= (ImageView) view.findViewById(R.id.imageView);
        Picasso.with(context)
                .load(cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH))
                .into(imageView);


    }
}
