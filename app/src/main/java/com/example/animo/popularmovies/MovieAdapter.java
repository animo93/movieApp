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
import android.widget.TextView;

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
        ViewHolder viewHolder= (ViewHolder) view.getTag();



    }

    public static class ViewHolder {
        public final TextView songName;
        public final TextView artistName;
        public ViewHolder(View view){
            songName= (TextView) view.findViewById(R.id.song_name);
            artistName= (TextView) view.findViewById(R.id.artist_name);
        }

    }
}
