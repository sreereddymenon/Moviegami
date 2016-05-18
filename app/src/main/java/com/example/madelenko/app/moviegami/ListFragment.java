package com.example.madelenko.app.moviegami;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madelenko.app.moviegami.datalayer.MovieProvider;
import com.example.madelenko.app.moviegami.datalayer.MovieTables;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment implements FetchMoviesTask.delegate {

    private static final String QUERY_TYPE = "query type";
    public static final int POPULAR = 0;
    public static final int HIGHEST_RATED = 1;
    public static final int FAVORITES = 2;


    private RecyclerView mRecyclerView;
    private int mListType;


    public ListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mListType = this.getArguments().getInt(QUERY_TYPE);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.movie_list,container,false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_list);
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);

        if (mListType == HIGHEST_RATED || mListType == POPULAR) {
            requestMovies();
        } else if (mListType == FAVORITES) {
            loadMoviesFromDb();
        }

        return rootView;
    }

    private void loadMoviesFromDb() {
        Cursor cursor = getActivity().getContentResolver().query(
                MovieProvider.Movies.MOVIES,
                null,
                null,
                null,
                null
        );
        if (cursor == null) {return;}
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
            loadResourcesFromDb(Movie.TRAILER,movie);
            loadResourcesFromDb(Movie.REVIEW,movie);
        }
        processMovies(movies);
        cursor.close();
    }

    private void loadResourcesFromDb(int resourceType, Movie movie) {
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
        Cursor cursor = getActivity().getContentResolver()
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

    private void addResourcesToMovie(int resourceType, @NonNull Cursor cursor, Movie movie) {
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
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new MovieAdapter((MovieListActivity) getActivity()));
    }

    private void requestMovies() {
        if (mRecyclerView.getAdapter().getItemCount() == 0) {
            new FetchMoviesTask(this,getContext()).execute(mListType);
        }
    }

    @Override
    public void processMovies(Movie[] movies) {
        MovieAdapter movieAdapter = (MovieAdapter)mRecyclerView.getAdapter();
        movieAdapter.setMovies(movies);
        movieAdapter.notifyDataSetChanged();
    }
}
