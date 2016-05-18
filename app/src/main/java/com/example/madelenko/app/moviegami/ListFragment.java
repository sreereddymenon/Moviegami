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

/**
 * UI component used to display a recyclerview containing movie objects. It can fetch
 * the movies asynchronously from the internet or it can process and display the movies received
 * from another source through its implementation of the FetchMoviesTask.Delegate interface.
 */
public class ListFragment extends Fragment implements FetchMoviesTask.Delegate {

    private static final String QUERY_TYPE = "query type";
    public static final int POPULAR = 0;
    public static final int HIGHEST_RATED = 1;
    public static final int FAVORITES = 2;


    private RecyclerView mRecyclerView;
    private int mMovieType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mMovieType = this.getArguments().getInt(QUERY_TYPE);    // Decides what movies to fetch.
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

        /*
         * If the fragment corresponds to favorite movies, the data is retrieved from our
         * database. If not, it is fetched from the internet.
         */
        if (mMovieType == HIGHEST_RATED || mMovieType == POPULAR) {
            requestMovies();
        } else if (mMovieType == FAVORITES) {
            Movie[] movies = Utility.loadMoviesFromDb(getContext());
            processMovies(movies);
        }

        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new MovieAdapter((MovieListActivity) getActivity()));
    }

    private void requestMovies() {
        if (mRecyclerView.getAdapter().getItemCount() == 0) {
            new FetchMoviesTask(this,getContext()).execute(mMovieType);
        }
    }

    @Override
    public void processMovies(Movie[] movies) {
        MovieAdapter movieAdapter = (MovieAdapter)mRecyclerView.getAdapter();
        movieAdapter.setMovies(movies);
        movieAdapter.notifyDataSetChanged();
    }
}
