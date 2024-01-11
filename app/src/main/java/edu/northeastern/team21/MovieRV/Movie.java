package edu.northeastern.team21.MovieRV;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("Title")
    private final String title;

    @SerializedName("Poster")
    private final String poster;

    @SerializedName("Year")
    private final String year;

    @SerializedName("Type")
    private final String type;

    @SerializedName("imdbID")
    private final String imdbId;


    public Movie(String title, String imageUrl, String year, String type, String imdbId) {
        this.title = title;
        this.poster = imageUrl;
        this.year = year;
        this.type = type;
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public String getImdbId() {
        return imdbId;
    }
}
