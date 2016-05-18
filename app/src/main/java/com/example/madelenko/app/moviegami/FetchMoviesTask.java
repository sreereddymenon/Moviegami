package com.example.madelenko.app.moviegami;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

 /*
 * Asynchronously */

final class FetchMoviesTask extends AsyncTask<Integer, Void, Movie[]> {


    private static final String API_KEY = "api_key";

    private Context mContext;
    private ListFragment mFragment;

    public FetchMoviesTask(ListFragment fragment) {
        this.mFragment = fragment;
        this.mContext = fragment.getContext();
    }

    @Override
    protected Movie[] doInBackground(Integer... params) {

        int queryType = params[0];

        Uri movieUri = makeUri(queryType);
        URL movieUrl;
        HttpURLConnection connection = null;
        String resultString;
        Movie[] movieArray = new Movie[20];

        try {
            movieUrl = new URL(movieUri.toString());
            connection = (HttpURLConnection) movieUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            resultString = downloadResources(connection);

            movieArray = parseJSONmovies(resultString);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return movieArray;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        mFragment.setMovies(movies);
        fetchResources(movies);
        super.onPostExecute(movies);
    }

    private Uri makeUri(int queryType) {
        String path;
        switch (queryType) {
            case ListFragment.POPULAR:
                path = "popular";
                break;
            case ListFragment.HIGHEST_RATED:
                path = "top_rated";
                break;
            default:
                throw new IllegalArgumentException("Undefined query type");
        }
        return Uri.parse("http://api.themoviedb.org/3/movie/")
                .buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_KEY,mContext.getString(R.string.api_key))
                .build();
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
                        .appendPath(Movie.THUMBNAIL_SIZE)
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

    @NonNull
    private String downloadResources(HttpURLConnection resourceConnection) {
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

    void fetchResources(Movie[] movies) {
        for (int i=0;i<movies.length;i++) {
            new FetchResourcesTask(mFragment).execute(movies[i]);
        }
    }
}