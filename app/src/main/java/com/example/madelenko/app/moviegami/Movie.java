package com.example.madelenko.app.moviegami;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class Movie implements Parcelable{

    private int movieId;
    private String originalTitle;
    private String posterPath;
    private String synopsis;
    private double userRating;
    private String releaseDate;
    private ArrayList<String> trailerList;
    private ArrayList<Pair<String,String>> reviewList;

    public static final int TRAILER = 1001;
    public static final int REVIEW = 2002;
    public static final String THUMBNAIL_SIZE = "w342";

    private int mReviewIndex = 0;
    private String youtubePrefix = "https://www.youtube.com/watch?v=";


    private Movie(int movieId, String originalTitle, String posterPath,
                 String releaseDate, String synopsis, double userRating) {
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.trailerList = null;
        this.reviewList = null;
    }

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

    protected List getReviewList() {

        return reviewList==null? null : (ArrayList) reviewList.clone();
    }

    protected String getSynopsis() {
        return synopsis;
    }

    protected List getTrailerList() {
        return trailerList==null? null : (ArrayList) trailerList.clone();
    }

    protected double getUserRating() {
        return userRating;
    }

    //Setters

    void setReviews(ArrayList<Pair<String, String>> reviewList) {
        if (this.reviewList == null) {
            this.reviewList = reviewList;
        }
    }

    void setTrailers(ArrayList<String> trailerList) {
        if (this.trailerList == null) {
            this.trailerList = trailerList;
        }
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", originalTitle='" + originalTitle + '\'' +
                '}';
    }

    public boolean hasTrailers() {
        return getTrailerList() !=null && getTrailerList().size() > 0;
    }

    public boolean hasReviews() {
        return getReviewList() !=null && getReviewList().size() > 0;
    }

    public void nextReview() {
        if (!hasReviews()) return;
        mReviewIndex = (mReviewIndex +1) % getReviewList().size();
    }

    private Pair<String,String> currentReview() {
        if (!hasReviews()) return null;
        return (Pair<String,String>) reviewList.get(mReviewIndex);
    }

    public String currentReviewAuthor() {
        return currentReview()==null? null : reviewList.get(mReviewIndex).first;
    }

    public String currentReviewContent() {
        return currentReview()==null? null : reviewList.get(mReviewIndex).second;
    }

    public String trailerAtPosition(int position) {
        return hasTrailers()? youtubePrefix + trailerList.get(position) : null;
    }


    // Implementation of Parcelable

    private Movie(Parcel in) {
        movieId = in.readInt();
        originalTitle = in.readString();
        posterPath = in.readString();
        synopsis = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
        trailerList = (ArrayList<String>) loadTrailers(in);
        reviewList = (ArrayList<Pair<String,String>>) loadReviews(in);
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
        writeTrailersToParcel(dest);
        writeReviewsToParcel(dest);
    }

    private void writeTrailersToParcel(Parcel dest) {
        if (this.trailerList != null && this.trailerList.size()>0) {
            dest.writeSerializable(trailerList);
        }
    }

    public List<String> loadTrailers(Parcel in) {
        List<String> result;
        result = (ArrayList<String>) in.readSerializable();
        return result;
    }

    public void writeReviewsToParcel(Parcel dest) {
        if (this.reviewList != null && this.reviewList.size()>0) {

            List<String> authors= new ArrayList<>();
            List<String> content = new ArrayList<>();

            for (int i=0;i<reviewList.size();i++) {
                Pair<String,String> pair = reviewList.get(i);
                authors.add(pair.first);
                content.add(pair.second);
            }

            dest.writeStringList(authors);
            dest.writeStringList(content);
        }
    }

    public List<Pair<String, String>> loadReviews(Parcel in) {
        List<String> authors = new ArrayList<>();
        List<String> content = new ArrayList<>();
        List<Pair<String,String>> result = new ArrayList<>();

        in.readStringList(authors);
        in.readStringList(content);

        for (int i=0;i<authors.size();i++) {
            result.add(new Pair<String, String>(authors.get(i),content.get(i)));
        }
        return result;
    }

}
