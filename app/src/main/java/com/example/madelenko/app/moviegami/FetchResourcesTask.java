package com.example.madelenko.app.moviegami;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchResourcesTask extends AsyncTask<Movie, Void, Void> {

    private static final int TRAILERS = 1000;
    private static final int REVIEWS = 2000;
    private static final String API_KEY = "api_key";

    private Context mContext;

    public FetchResourcesTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Movie... params) {

        Movie movie = params[0];
        HttpURLConnection trailerConnection;
        HttpURLConnection reviewConnection;

        trailerConnection = makeResourceConnection(movie,TRAILERS);
        reviewConnection = makeResourceConnection(movie,REVIEWS);

        String trailerString = downloadResources(trailerConnection);
        String reviewString = downloadResources(reviewConnection);

        List<String> trailerList = parseResourceJSON(TRAILERS,trailerString);
        List<Pair<String,String>> reviewList = parseResourceJSON(REVIEWS, reviewString);

        movie.setTrailers((ArrayList)trailerList);
        movie.setReviews((ArrayList) reviewList);

        return null;
    }

    @NonNull
    private List parseResourceJSON(int resourceType, String resourceString) {
        List resultList = new ArrayList();
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(resourceString);
            jsonArray = jsonObject.getJSONArray("results");

            switch (resourceType) {
                case TRAILERS:
                    for (int i=0;i<jsonArray.length();i++) {
                        resultList.add(jsonArray.getJSONObject(i).getString("key"));
                    }
                    break;
                case REVIEWS:
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

    private HttpURLConnection makeResourceConnection(Movie movie, int resourceType) {
        URL url = null;
        Uri uri;
        HttpURLConnection connection = null;
        Uri.Builder builder = Uri.parse("http://api.themoviedb.org/3/movie")
                .buildUpon()
                .appendPath(String.valueOf(movie.getMovieId()));

        switch (resourceType) {
            case TRAILERS:
                uri = builder.appendPath("videos")
                        .appendQueryParameter(API_KEY,mContext.getString(R.string.api_key))
                        .build();
                break;
            case REVIEWS:
                uri = builder.appendPath("reviews")
                        .appendQueryParameter(API_KEY,mContext.getString(R.string.api_key))
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

    @NonNull private String downloadResources(HttpURLConnection resourceConnection) {
        BufferedReader reader = null;
        InputStreamReader streamReader = null;
        StringBuilder builder = new StringBuilder();
        String line = null;
        try {
            streamReader = new InputStreamReader(resourceConnection.getInputStream());
            reader = new BufferedReader(streamReader);
            while ((line = reader.readLine()) != null) {
                builder.append(line + "%n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                streamReader.close();
                resourceConnection.disconnect();
            } catch (IOException|NullPointerException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
