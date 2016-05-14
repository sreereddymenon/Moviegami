package com.example.madelenko.app.moviegami;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ListFragment extends Fragment {

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
        }

        return rootView;
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new MovieAdapter((MovieListActivity) getActivity()));
    }

    void setMovies(Movie[] movies) {
        MovieAdapter movieAdapter = (MovieAdapter)mRecyclerView.getAdapter();
        movieAdapter.setMovies(movies);
        movieAdapter.notifyDataSetChanged();
    }

    private void requestMovies() {
        if (mRecyclerView.getAdapter().getItemCount() == 0) {
            new FetchMoviesTask(this).execute(mListType);
        }
    }
}
