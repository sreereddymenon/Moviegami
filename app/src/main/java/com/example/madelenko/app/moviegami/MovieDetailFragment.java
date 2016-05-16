package com.example.madelenko.app.moviegami;

import android.content.ContentValues;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.madelenko.app.moviegami.datalayer.MovieProvider;
import com.example.madelenko.app.moviegami.datalayer.MovieTables.MovieColumns;
import com.example.madelenko.app.moviegami.datalayer.MovieTables.TrailerColumns;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieDetailFragment extends Fragment {

    private Movie mMovie;
    private AppCompatActivity mActivity;
    private YouTubePlayer mPlayer;
    private LinearLayout mLinearLayout;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mActivity = (AppCompatActivity)getActivity();
        mMovie = getArguments().getParcelable(MovieListActivity.MOVIE);
        ArrayList<String> trailers = getArguments().getStringArrayList(MovieAdapter.TRAILERS_PATH);
        mMovie.setTrailers(trailers);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.card_linear_layout);


        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        mActivity.setSupportActionBar(toolbar);



        // Show the Up button in the action bar.
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        insertMovieIntoDatabase();
                        insertResourcesIntoDatabase(Movie.TRAILER);
//                        int result = insertResourcesIntoDatabase(Movie.REVIEW);

//                        Snackbar.make(view, "inserted: " + result, Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                    } catch (SQLException e) {}
                }
            });
        }

        if (mMovie != null) {
            final CollapsingToolbarLayout appBarLayout =
                    (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mMovie.getOriginalTitle());
                Picasso.with(mActivity).load(mMovie.getPosterPath())
                        .into((ImageView) appBarLayout.findViewById(R.id.image_stretch_detail));
            }
        }

        YouTubePlayerSupportFragment fragment = new YouTubePlayerSupportFragment();
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.youtube_fragment_target, fragment)
                .commit();

        fragment.initialize("AIzaSyAp8HgLng6TaV0xlcWN3iv8s_S_XZZGfBs", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mPlayer = youTubePlayer;
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
        return rootView;
    }

    private int insertResourcesIntoDatabase(int resourceType) {
        List<String> resources = null;
        Uri insertionUri = null;

        switch (resourceType) {
            case Movie.TRAILER:
                resources = mMovie.getTrailerList();
                insertionUri = MovieProvider.Trailers.withIdTrailers(mMovie.getMovieId());
                break;
            case Movie.REVIEW:
                resources = mMovie.getReviewList();
                insertionUri = MovieProvider.Reviews.withIdReviews(mMovie.getMovieId());
                break;
            default:
                throw new IllegalArgumentException("Undefined resource type.");
        }

        if (resources != null) {
            ContentValues[] contentValuesArray = new ContentValues[resources.size()];
            for (int i=0;i<resources.size();i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TrailerColumns.MOVIE_ID, mMovie.getMovieId());
                contentValues.put(TrailerColumns.VIDEO, resources.get(i));
                contentValuesArray[i] = contentValues;
            }
            return mActivity.getContentResolver().bulkInsert(
                    insertionUri,
                    contentValuesArray
            );
        }
        return 0;
    }

    private Uri insertMovieIntoDatabase() {
        ContentValues values = new ContentValues();
        values.put(MovieColumns._ID, mMovie.getMovieId());
        values.put(MovieColumns.TITLE, mMovie.getOriginalTitle());
        values.put(MovieColumns.POSTER, mMovie.getPosterPath());
        values.put(MovieColumns.SYNOPSIS, mMovie.getSynopsis());
        values.put(MovieColumns.RATING, mMovie.getUserRating());
        values.put(MovieColumns.DATE, mMovie.getReleaseDate());
        return mActivity.getContentResolver().insert(MovieProvider.Movies.MOVIES, values);
    }
}
