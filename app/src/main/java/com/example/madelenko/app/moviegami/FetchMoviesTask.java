package com.example.madelenko.app.moviegami;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Asynchronously fetches movies from the endpoint API. Delegates the processing
 * of the results to an object implementing its inner interface "Delegate". On completion,
 * launches tasks to retrieve movie trailers and reviews.
 */

final class FetchMoviesTask extends AsyncTask<Integer, Void, Movie[]> {

    private Context mContext;
    private Delegate mDelegate;

    public FetchMoviesTask(Delegate delegate, Context context) {
        this.mDelegate = delegate;
        this.mContext = context;
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

            resultString = Utility.downloadResources(connection);

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
        mDelegate.processMovies(movies);    // Object responsible for processing results
        fetchResources(movies);             // Update trailers and reviews of movies
        super.onPostExecute(movies);
    }

    /*
     * Helper method that creates a URI with the appropriate
     * path segments to retrieve the correct movie objects.
     * @Param: A constant defining the type of movies to query for.
     * @Return: An Uri from which we will retrieve the movies.
     */
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
        return Uri.parse(Utility.MOVIES_BASE_URL)
                .buildUpon()
                .appendPath(path)
                .appendQueryParameter(Utility.API_KEY,mContext.getString(R.string.api_key))
                .build();
    }

    /*
     * Helper method that converts a JSON string into an array of Movie objects.
     * @Param: String in JSON format containing Movie object structures.
     * @Return: An array of Movie objects.
     */
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
                uri = Uri.parse(Utility.PICTURE_BASE_URL).buildUpon()
                        .appendPath(Movie.SIZE_THUMBNAIL)
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

    /*
     * Launches a task to fetch and update trailers
     * and reviews for each movie in the parameter array
     */
    private void fetchResources(Movie[] movies) {
        for (Movie movie : movies) {
            new FetchResourcesTask(mContext).execute(movie);
        }
    }

    /**
     * Member interface of the FetchMoviesTask class that defines how to process
     * the movies retrieved by it.
     */
    public interface Delegate {
        void processMovies(Movie[] movies);
    }
}