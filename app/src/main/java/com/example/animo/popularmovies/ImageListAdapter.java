package com.example.animo.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by animo on 2/8/16.
 */
public class ImageListAdapter extends BaseAdapter {
    private Context context;
    private String[] imageUrls;

    public ImageListAdapter(Context context, String[] imageUrls) {
        super();
        this.context = context;
        this.imageUrls = imageUrls;


    }

    public void add(String[] strings) {
        this.imageUrls = strings;
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

        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.movie_list, parent, false);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Picasso
                .with(context)
                .load(imageUrls[position])
                .into(imageView);


        return view;
    }
}
