package com.example.animo.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by animo on 27/10/16.
 */
public class MovieAdapter extends CursorAdapter{
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.movie_list,parent,false);

        ViewHolder viewHolder=new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.e("bind view","poster "+cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH));
        File image=new File(cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH));
        ViewHolder viewHolder= (ViewHolder) view.getTag();
        Picasso.with(context)
                .load(image)
                .into(viewHolder.imageView);


    }

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view){
            imageView= (ImageView) view.findViewById(R.id.imageView);
        }

    }
}
