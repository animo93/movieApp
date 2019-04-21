package com.example.animo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by animo on 3/8/16.
 */
public class MovieData implements Parcelable {
    String movieId;
    String movieName;
    String mTwoPane;

    protected MovieData(String in,String movieName,String mTwoPane) {
        super();
        this.movieId=in;
        this.movieName=movieName;
        this.mTwoPane=mTwoPane;
    }
    protected MovieData(Parcel in){

        this.movieId=in.readString();
        this.movieName=in.readString();
        this.mTwoPane=in.readString();
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
        dest.writeString(movieName);
        dest.writeString(mTwoPane);

    }
}
