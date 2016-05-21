package com.example.madelenko.app.moviegami;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.example.madelenko.app.moviegami.datalayer.MovieProvider;
import com.example.madelenko.app.moviegami.datalayer.MovieTables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Class containing utility methods and parameters.
 */
public final class Utility {

    public static final String API_KEY = "api_key";
    public static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String PICTURE_BASE_URL = "http://image.tmdb.org/t/p";




    /*
     * Method that downloads the results of an Http connection and saves them as a String.
     * @Param: HttpUrlConnection from which we will download our resources.
     * @Return: A string with the contents of the connection
     */
    @NonNull
    public static String downloadResources(@NonNull HttpURLConnection resourceConnection) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    /*
     * Retrieves an array of Movie objects from the database, or null, if there aren't any movies.
     * @Param: Context context.
     * @Return: Movie[] or null if the database is empty.
     */
    @Nullable
    public static Movie[] loadMoviesFromDb(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MovieProvider.Movies.MOVIES,
                null,
                null,
                null,
                null
        );
        if (cursor == null) {return null;}
        Movie[] movies = new Movie[cursor.getCount()];
        int counter = 0;
        while (cursor.moveToNext()) {
            Movie movie = Movie.makeMovie(
                    cursor.getInt(cursor.getColumnIndex(MovieTables.MovieColumns._ID)),
                    cursor.getString(cursor.getColumnIndex(MovieTables.MovieColumns.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MovieTables.MovieColumns.POSTER)),
                    cursor.getString(cursor.getColumnIndex(MovieTables.MovieColumns.DATE)),
                    cursor.getString(cursor.getColumnIndex(MovieTables.MovieColumns.SYNOPSIS)),
                    cursor.getFloat(cursor.getColumnIndex(MovieTables.MovieColumns.RATING))
            );
            movies[counter++] = movie;
            loadResourcesFromDb(Movie.TRAILER,movie,context);
            loadResourcesFromDb(Movie.REVIEW,movie, context);
        }
        cursor.close();
        return movies;
    }


    /*
     * Helper method to fetch trailers or reviews from the database and adding them to
     * the movie objects retrieved by the loadMoviesFromDb method. The type of resource to be
     * fetched depends on an integer flag passed as a parameter.
     * @Param: int resourceType, a flag describing the type of resource to be fetched; Movie movie,
     * Context context.
     * @Return: void.
     */
    private static void loadResourcesFromDb(int resourceType, Movie movie,Context context) {
        Uri contentUri = null;
        switch (resourceType) {
            case Movie.TRAILER:
                contentUri = MovieProvider.Trailers.withIdTrailers(movie.getMovieId());
                break;
            case Movie.REVIEW:
                contentUri = MovieProvider.Reviews.withIdReviews(movie.getMovieId());
                break;
            default:
                throw new IllegalArgumentException("Unidentified type of resource.");
        }
        Cursor cursor = context.getContentResolver()
                .query(
                        contentUri,
                        null,
                        null,
                        null,
                        null
                );
        if (cursor == null) {return;}
        addResourcesToMovie(resourceType, cursor,movie);
        cursor.close();
    }

    /*
     * Helper method to include the resources fetched by loadResourcesFromDb to a Movie object.
     * It takes a flag as a parameter to indicate wether it has to include reviews or trailers.
     * @Param: int resourceType, a flag indicating the type of resources to bind; Cursor cursor, the
     * cursor from which the resources will be extracted; Movie movie, the object to which the
     * resources will be bound.
     * @Return: void.
     */
    private static void addResourcesToMovie(int resourceType, @NonNull Cursor cursor, Movie movie) {
        switch (resourceType) {
            case Movie.TRAILER:
                ArrayList<String> trailerList = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    trailerList.add(
                            cursor.getString(cursor.getColumnIndex(
                                    MovieTables.TrailerColumns.VIDEO))
                    );
                }
                movie.setTrailers(trailerList);
                break;
            case Movie.REVIEW:
                ArrayList<Pair<String,String>> reviewList = new ArrayList<Pair<String, String>>();
                while (cursor.moveToNext()) {
                    reviewList.add(new Pair<String, String>(
                            cursor.getString(cursor.getColumnIndex(
                                    MovieTables.ReviewColumns.AUTHOR)),
                            cursor.getString(cursor.getColumnIndex(
                                    MovieTables.ReviewColumns.CONTENT))

                    ));
                }
                movie.setReviews(reviewList);
                break;
            default:
                throw new IllegalArgumentException("Unidentified type of resource.");
        }
        cursor.close();
    }

    public static Uri insertMovieIntoDatabase(Movie movie, Context context) {
        ContentValues values = new ContentValues();
        values.put(MovieTables.MovieColumns._ID, movie.getMovieId());
        values.put(MovieTables.MovieColumns.TITLE, movie.getOriginalTitle());
        values.put(MovieTables.MovieColumns.POSTER, movie.getPosterPath());
        values.put(MovieTables.MovieColumns.SYNOPSIS, movie.getSynopsis());
        values.put(MovieTables.MovieColumns.RATING, movie.getUserRating());
        values.put(MovieTables.MovieColumns.DATE, movie.getReleaseDate());
        return context.getContentResolver().insert(MovieProvider.Movies.MOVIES, values);
    }

    public static int insertResourcesIntoDatabase(int resourceType, Movie movie, Context context) {
        List resources = null;
        Uri insertionUri = null;

        switch (resourceType) {
            case Movie.TRAILER:
                resources = movie.getTrailerList();
                insertionUri = MovieProvider.Trailers.withIdTrailers(movie.getMovieId());
                if (resources != null) {
                    ContentValues[] contentValuesArray = new ContentValues[resources.size()];
                    for (int i=0;i<resources.size();i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MovieTables.TrailerColumns.MOVIE_ID, movie.getMovieId());
                        contentValues.put(MovieTables.TrailerColumns.VIDEO, (String)resources.get(i));
                        contentValuesArray[i] = contentValues;
                    }
                    return context.getContentResolver().bulkInsert(
                            insertionUri,
                            contentValuesArray
                    );
                }
                break;
            case Movie.REVIEW:
                resources = movie.getReviewList();
                insertionUri = MovieProvider.Reviews.withIdReviews(movie.getMovieId());
                if (resources != null) {
                    ContentValues[] contentValuesArray = new ContentValues[resources.size()];
                    for (int i=0;i<resources.size();i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MovieTables.ReviewColumns.MOVIE_ID, movie.getMovieId());
                        contentValues.put(MovieTables.ReviewColumns.AUTHOR,(String) ((Pair)resources.get(i)).first);
                        contentValues.put(MovieTables.ReviewColumns.CONTENT,(String) ((Pair)resources.get(i)).second);
                        contentValuesArray[i] = contentValues;
                    }
                    return context.getContentResolver().bulkInsert(
                            insertionUri,
                            contentValuesArray
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Undefined resource type.");
        }
        return 0;
    }


    public static boolean deviceConnected(Context context) {
        ConnectivityManager networkManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = networkManager.getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.isConnected();
    }
}
