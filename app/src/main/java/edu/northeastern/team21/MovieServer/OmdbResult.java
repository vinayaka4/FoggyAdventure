package edu.northeastern.team21.MovieServer;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import edu.northeastern.team21.MovieRV.Movie;

public class OmdbResult {

    @SerializedName("Search")
    public final List<Movie> search;

    @SerializedName("totalResults")
    public final int totalResults;

    @SerializedName("Response")
    public final boolean response;

    @SerializedName("Error")
    public final String error;


    public OmdbResult(List<Movie> search, int totalResults, boolean response, String error) {
        this.search = search;
        this.totalResults = totalResults;
        this.response = response;
        this.error = error;
    }
}
