package com.example.madelenko.app.moviegami;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Non-extensible class that represents a movie object. Objects of this class can be passed from
 * one activity to another after the first one is destroyed through its implementation of the
 * Parcelable interface.
 */
public final class Movie implements Parcelable{

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
    public static final String SIZE_THUMBNAIL = "w342";
    public static final String SIZE_LARGE = "w780";
    public static final String SIZE_ORIGINAL = "original";

    // Marks the review currently accessed by currentReviewAuthor and currentReviewContent
    private int mReviewIndex = 0;
    private String youtubePrefix = "https://www.youtube.com/watch?v=";


    // Private constructor called by static factory methods
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

    // Public factory method to create a simple movie, without trailers or reviews.
    public static Movie makeMovie(int movieId, String originalTitle, String posterPath,
                           String releaseDate, String synopsis, double userRating) {

        return new Movie(movieId, originalTitle, posterPath, releaseDate, synopsis, userRating);
    }

    // Public factory method to create a simple movie and set its reviews and trailers.
    public static Movie makeMovieWithResources(
            int movieId,
            String originalTitle,
            String posterPath,
            String releaseDate,
            String synopsis,
            double userRating,
            ArrayList<String> trailerList,
            ArrayList<Pair<String,String>> reviewList) {

        Movie movie = new Movie(movieId,originalTitle,posterPath,releaseDate,synopsis,userRating);
        movie.setTrailers(trailerList);
        movie.setReviews(reviewList);

        return movie;
    }


    // Getter Methods
    public int getMovieId() {return movieId;}

    public String getOriginalTitle() {return originalTitle;}

    public String getPosterPath() {return posterPath;}

    public String getReleaseDate() {return releaseDate;}

    public String getSynopsis() {return synopsis;}

    public double getUserRating() {return userRating;}

    // We return a clone of the original list to prevent the client from modifying the original list.
    @Nullable
    public List getReviewList() {return reviewList==null? null : (ArrayList) reviewList.clone();}

    @Nullable
    public List getTrailerList() {return trailerList==null? null : (ArrayList) trailerList.clone();}


    //Setter Methods
    public void setReviews(ArrayList<Pair<String, String>> reviewList) {
        if (this.reviewList == null) {
            this.reviewList = reviewList;
        }
    }

    public void setTrailers(ArrayList<String> trailerList) {
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

    /*
     * Moves to the index of the next review. Useful to iterate over review objects
     * and offer its author and content variables via currentReviewAuthor and currentReviewContent.
     */
    public void nextReview() {
        if (!hasReviews()) return;
        mReviewIndex = (mReviewIndex +1) % getReviewList().size();
    }

    @Nullable
    private Pair<String,String> currentReview() {
        if (!hasReviews()) return null;
        return (Pair<String,String>) reviewList.get(mReviewIndex);
    }

    /**
     * Returns the author field of the review at the current position, or null
     * if the list of reviews is empty.
     */
    @Nullable
    public String currentReviewAuthor() {
        return currentReview()==null? null : reviewList.get(mReviewIndex).first;
    }
    /**
     * Returns the content field of the review at the current position, or null
     * if the list of reviews is empty.
     */
    @Nullable
    public String currentReviewContent() {
        return currentReview()==null? null : reviewList.get(mReviewIndex).second;
    }

    /**
     * Returns a String containing the Url of the Trailer at the specified position, or null
     * if the movie doesn't have any trailers.
     */
    @Nullable
    public String trailerAtPosition(int position) {
        return hasTrailers()? youtubePrefix + trailerList.get(position) : null;
    }




    // Parcelable constructor to be used by the creator object.
    private Movie(Parcel in) {
        movieId = in.readInt();
        originalTitle = in.readString();
        posterPath = in.readString();
        synopsis = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
        trailerList = (ArrayList<String>) in.readSerializable();
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

    // If the Movie has trailers, they are written to the parcel.
    private void writeTrailersToParcel(Parcel dest) {
        if (hasTrailers()) {
            dest.writeSerializable(trailerList);
        }
    }

    // If the Movie has reviews, they are written to the parcel
    private void writeReviewsToParcel(Parcel dest) {
        if (hasReviews()) {
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

    // Retrieves the reviews from the parcel and stores them in the correct format.
    private List<Pair<String, String>> loadReviews(Parcel in) {
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
