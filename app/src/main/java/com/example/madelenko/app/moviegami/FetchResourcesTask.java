package com.example.madelenko.app.moviegami;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronously fetches trailers and reviews from the endpoint API and updates the movie
 * passed as a parameter.
 */
public final class FetchResourcesTask extends AsyncTask<Movie, Void, Void> {

    private Context mContext;

    public FetchResourcesTask(Context context) {
        this.mContext = context;
    }

    /*
     * Method called in background thread to open Http connections to the API,
     * download and parse a String containing the trailers and reviews and finally
     * update the movie with them.
     * @Param: Movie object to be updated.
     * @Return: void.
     */
    @Override
    protected Void doInBackground(Movie... params) {

        Movie movie = params[0];
        HttpURLConnection trailerConnection;
        HttpURLConnection reviewConnection;

        trailerConnection = makeResourceConnection(movie,Movie.TRAILER);
        reviewConnection = makeResourceConnection(movie,Movie.REVIEW);

        String trailerString = Utility.downloadResources(trailerConnection);
        String reviewString = Utility.downloadResources(reviewConnection);

        List<String> trailerList = parseResourceJSON(Movie.TRAILER, trailerString);
        List<Pair<String,String>> reviewList = parseResourceJSON(Movie.REVIEW, reviewString);


        movie.setTrailers((ArrayList<String>)trailerList);
        movie.setReviews((ArrayList<Pair<String,String>>) reviewList);

        return null;
    }

    /*
     * Parses the JSON string passed as a parameter according to the integer flag passed. Returns a
     * list containing the processed resources.
     * @Params: String resourceString, int resourceType
     * @Return: List containing the resources.
     */
    @NonNull
    private List parseResourceJSON(int resourceType, String resourceString) {
        List resultList = new ArrayList();
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(resourceString);
            jsonArray = jsonObject.getJSONArray("results");

            switch (resourceType) {
                case Movie.TRAILER:
                    for (int i=0;i<jsonArray.length();i++) {
                        resultList.add(jsonArray.getJSONObject(i).getString("key"));
                    }
                    break;
                case Movie.REVIEW:
                    for (int i=0;i<jsonArray.length();i++) {
                        String author = jsonArray.getJSONObject(i).getString("author");
                        String content = jsonArray.getJSONObject(i).getString("content");
                        resultList.add(new Pair(author,content));
                    }
                    break;
            }
        } catch (JSONException e) {e.printStackTrace();}
        return resultList;
    }

    /*
     * Builds an Uri based on the resource type passed as an integer flag and the movie
     * for which the resources are fetched. Returns an HttpURLConnection.
     * @Param: Movie movie, int resourceType.
     * @Return: HttpURLConnection
     */
    @NonNull
    private HttpURLConnection makeResourceConnection(Movie movie, int resourceType) {
        URL url = null;
        Uri uri;
        HttpURLConnection connection = null;
        Uri.Builder builder = Uri.parse(Utility.MOVIES_BASE_URL)
                .buildUpon()
                .appendPath(String.valueOf(movie.getMovieId()));

        switch (resourceType) {
            case Movie.TRAILER:
                uri = builder.appendPath("videos")
                        .appendQueryParameter(Utility.API_KEY,BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                        .build();
                break;
            case Movie.REVIEW:
                uri = builder.appendPath("reviews")
                        .appendQueryParameter(Utility.API_KEY,BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                        .build();
                break;
            default:
                throw new IllegalArgumentException("Invalid resource type");
        }
        try {
            url = new URL(uri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
