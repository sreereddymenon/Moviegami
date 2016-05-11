package com.example.madelenko.app.moviegami;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class FetchMoviesService extends IntentService {

    private static final int POPULAR = 0;
    private static final int HIGHEST_RATED = 1;
    private static final int TRAILERS = 1000;
    private static final int REVIEWS = 2000;
    private static final String RECEIVER = "receiver key";
    private static final String API_KEY = "api_key";
    private static final String SIZE = "w342";
    private ResultProcessor mProcessor;


    FetchMoviesService() {
        super(FetchMoviesService.class.getCanonicalName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e("SERVICE", "NEW SERVICE CREATED, BE CAREFUL");

        mProcessor = MovieListActivity.mInstance;

        int queryType = intent.getIntExtra("query type", Integer.MAX_VALUE);
//        mReceiver = inflateReceiver(intent);

        Uri movieUri = makeUri(queryType);
        URL movieUrl;
        HttpURLConnection trailerConnection = null;
        HttpURLConnection reviewConnection = null;
        StringBuilder builder;
        HttpURLConnection connection = null;
        String resultString;

        try {
            movieUrl = new URL(movieUri.toString());
            connection = (HttpURLConnection) movieUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            resultString = downloadResources(connection);

            Movie movieArray[]= parseJSONmovies(resultString);

            for (int i=0; i<movieArray.length; i++) {
                trailerConnection = makeResourceConnection(movieArray[i],TRAILERS);
                reviewConnection = makeResourceConnection(movieArray[i],REVIEWS);

                String trailerString = downloadResources(trailerConnection);
                String reviewString = downloadResources(reviewConnection);

                Set<String> trailerSet = parseResourceJSON(TRAILERS,trailerString);
                Set<Pair<String,String>> reviewSet = parseResourceJSON(REVIEWS, reviewString);

                movieArray[i].setTrailers((HashSet)trailerSet);
                movieArray[i].setReviews((HashSet)reviewSet);
            }

//            mReceiver.notifyReceiver(new Bundle(),movieArray);

            mProcessor.processResult(movieArray);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                trailerConnection.disconnect();
                reviewConnection.disconnect();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private Uri makeUri(int queryType) {
        String path;
        switch (queryType) {
            case POPULAR:
                path = "popular";
                break;
            case HIGHEST_RATED:
                path = "top_rated";
                break;
            default:
                throw new IllegalArgumentException("Undefined query type");
        }
        return Uri.parse("http://api.themoviedb.org/3/movie/")
                .buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_KEY,getString(R.string.api_key))
                .build();

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
                        .appendQueryParameter(API_KEY,getString(R.string.api_key))
                        .build();
                break;
            case REVIEWS:
                uri = builder.appendPath("reviews")
                        .appendQueryParameter(API_KEY,getString(R.string.api_key))
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


    private Movie[] parseJSONmovies(String inputString) {

        String results = "results";
        String id = "id";
        String title = "original_title";
        String overview = "overview";
        String image = "poster_path";
        String date = "release_date";
        String rating = "vote_average";
        Movie movieArray[] = new Movie[20];
        Movie movie;

        try {
            JSONObject rootObject = new JSONObject(inputString);
            JSONArray array = rootObject.getJSONArray(results);

            Uri uri;
            String path;

            for (int i=0;i<array.length();i++) {
                JSONObject movieObject = array.getJSONObject(i);

                path = movieObject.getString(image);
                uri = Uri.parse("http://image.tmdb.org/t/p").buildUpon()
                        .appendPath(SIZE)
                        .appendEncodedPath(path)
                        .build();

                movie = Movie.makeMovie(
                        movieObject.getInt(id),
                        movieObject.getString(title),
                        uri.toString(),
                        movieObject.getString(date),
                        movieObject.getString(overview),
                        movieObject.getDouble(rating)
                );
                movieArray[i] = movie;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieArray;
    }

    @NonNull private Set parseResourceJSON(int resourceType, String resourceString) {
        Set resultSet = new HashSet();
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(resourceString);
            jsonArray = jsonObject.getJSONArray("results");

            switch (resourceType) {
                case TRAILERS:
                    for (int i=0;i<jsonArray.length();i++) {
                        resultSet.add(jsonArray.getJSONObject(i).getString("key"));
                    }
                    break;
                case REVIEWS:
                    for (int i=0;i<jsonArray.length();i++) {
                        String author = jsonArray.getJSONObject(i).getString("author");
                        String content = jsonArray.getJSONObject(i).getString("content");
                        resultSet.add(new Pair(author,content));
                    }
                    break;
            }
        } catch (JSONException e) {e.printStackTrace();}
        return resultSet;
    }

    interface ResultProcessor {
        void processResult(@NonNull Movie[] movies);
    }

}
