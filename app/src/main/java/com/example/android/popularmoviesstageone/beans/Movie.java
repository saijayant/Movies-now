package com.example.android.popularmoviesstageone.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by akshayshahane on 08/06/17.
 */

public class Movie  implements Parcelable{
    @SerializedName("title")
    String title;
    @SerializedName("release_date")
    String releaseDate;
    @SerializedName("poster_path")
    String moviePosterUrl;
    @SerializedName("vote_average")
    String ratings;
    @SerializedName("overview")
    String plotSynopsis;
    @SerializedName("id")
    String id;

    protected Movie(Parcel in) {
        title = in.readString();
        releaseDate = in.readString();
        moviePosterUrl = in.readString();
        ratings = in.readString();
        plotSynopsis = in.readString();
        id = in.readString();
    }
    public  Movie (){}

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(moviePosterUrl);
        parcel.writeString(ratings);
        parcel.writeString(id);
        parcel.writeString(plotSynopsis);



    }
}
