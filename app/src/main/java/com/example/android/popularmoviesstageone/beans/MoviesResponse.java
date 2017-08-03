package com.example.android.popularmoviesstageone.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by akshayshahane on 08/06/17.
 */

public class MoviesResponse {
    @SerializedName("results")
    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
