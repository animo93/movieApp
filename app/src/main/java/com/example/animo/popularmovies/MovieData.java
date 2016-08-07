package com.example.animo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by animo on 3/8/16.
 */
public class MovieData implements Parcelable {
    String movieId;

    protected MovieData(String in) {
        super();
        this.movieId=in;
    }
    protected MovieData(Parcel in){
        this.movieId=in.readString();
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
    }
}
