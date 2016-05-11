package com.example.madelenko.app.moviegami;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import java.util.HashSet;
import java.util.Set;

final class Movie implements Parcelable{

    private int movieId;
    private String originalTitle;
    private String posterPath;
    private String synopsis;
    private double userRating;
    private String releaseDate;
    private HashSet<String> trailerSet;
    private HashSet<Pair<String,String>> reviewSet;


    private Movie(int movieId, String originalTitle, String posterPath,
                 String releaseDate, String synopsis, double userRating) {
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.trailerSet = null;
        this.reviewSet = null;
    }

    // Package-private factory method to create movies with the service
    static Movie makeMovie(int movieId, String originalTitle, String posterPath,
                           String releaseDate, String synopsis, double userRating) {

        return new Movie(movieId, originalTitle, posterPath, releaseDate, synopsis, userRating);
    }

    // Getters

    protected int getMovieId() {
        return movieId;
    }

    protected String getOriginalTitle() {
        return originalTitle;
    }

    protected String getPosterPath() {
        return posterPath;
    }

    protected String getReleaseDate() {
        return releaseDate;
    }

    protected Set getReviewSet() {
        return (HashSet) reviewSet.clone();
    }

    protected String getSynopsis() {
        return synopsis;
    }

    protected Set getTrailerSet() {
        return (HashSet) trailerSet.clone();
    }

    protected double getUserRating() {
        return userRating;
    }

    //Setters

    void setReviews(HashSet<Pair<String, String>> reviewSet) {
        if (reviewSet == null) {
            this.reviewSet = reviewSet;
        }
        return;
    }

    void setTrailers(HashSet<String> trailerSet) {
        if (trailerSet == null) {
            this.trailerSet = trailerSet;
        }
        return;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", originalTitle='" + originalTitle + '\'' +
                '}';
    }

    // Implementation of Parcelable

    private Movie(Parcel in) {
        movieId = in.readInt();
        originalTitle = in.readString();
        posterPath = in.readString();
        synopsis = in.readString();
        userRating = in.readFloat();
        releaseDate = in.readString();
    }

    static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(synopsis);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
    }

}
