package com.example.madelenko.app.moviegami;

import android.content.ContentValues;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madelenko.app.moviegami.datalayer.MovieProvider;
import com.example.madelenko.app.moviegami.datalayer.MovieTables;
import com.example.madelenko.app.moviegami.datalayer.MovieTables.MovieColumns;
import com.example.madelenko.app.moviegami.datalayer.MovieTables.TrailerColumns;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.youtube.player.YouTubePlayer.*;


public class MovieDetailFragment extends Fragment {

    private Movie mMovie;
    private AppCompatActivity mActivity;
    private YouTubePlayer mPlayer;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

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
                        int result = insertResourcesIntoDatabase(Movie.REVIEW);
                    } catch (SQLException e) {}
                }
            });
        }

        if (mMovie != null) {
            final CollapsingToolbarLayout appBarLayout =
                    (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mMovie.getOriginalTitle());
                String hdPosterPath = mMovie.getPosterPath().replace("w342","w780");
                Picasso.with(mActivity).load(hdPosterPath)
                        .into((ImageView) appBarLayout.findViewById(R.id.image_stretch_detail));
            }
        }
        bindViewValues(rootView);
        loadYoutubeFragment();
        return rootView;
    }

    private void bindViewValues(View rootView) {
        TextView overviewContent = (TextView) rootView.findViewById(R.id.overview_content);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date_textview);
        TextView rating = (TextView) rootView.findViewById(R.id.user_rating_textview);
        TextView username = (TextView) rootView.findViewById(R.id.username_textview);
        TextView reviewText = (TextView) rootView.findViewById(R.id.review_body);

        overviewContent.setText(mMovie.getSynopsis());
        releaseDate.setText(mMovie.getReleaseDate());
        rating.setText(String.format("%.1f",mMovie.getUserRating()));

        if (mMovie.hasReviews()) {
            username.setText(mMovie.currentReviewAuthor());
            reviewText.setText(mMovie.currentReviewContent());
        }
    }

    private void loadYoutubeFragment() {
        YouTubePlayerSupportFragment fragment = new YouTubePlayerSupportFragment();
        fragment.setRetainInstance(true);
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.youtube_fragment_target, fragment)
                .commit();

        fragment.initialize("AIzaSyAp8HgLng6TaV0xlcWN3iv8s_S_XZZGfBs",
                new OnInitializedListener() {
            @Override
            public void onInitializationSuccess(Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                mPlayer = youTubePlayer;
                mPlayer.setPlayerStyle(PlayerStyle.CHROMELESS);
                mPlayer.addFullscreenControlFlag(
                        FULLSCREEN_FLAG_CONTROL_ORIENTATION |
                        FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
                List<String> trailerList = mMovie.getTrailerList();
                if (trailerList != null && trailerList.size()>0) {
                    mPlayer.cueVideos(trailerList);
                }
            }
            @Override
            public void onInitializationFailure(
                    Provider provider,
                    YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    void updateVideoStatus(View v) {
        int tag = v.getId();
        List<String> trailerList = mMovie.getTrailerList();
        if (trailerList == null || trailerList.size() <= 0) {return;}

        switch (tag) {
            case R.id.cardview_action_play:
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    ((ImageButton)v).setImageResource(R.drawable.ic_play_arrow_black_translucid);
                } else {
                    mPlayer.play();
                    ((ImageButton)v).setImageResource(R.drawable.ic_pause_black_translucid);
                }
                break;
            case R.id.cardview_action_next:
                if (mPlayer.hasNext()) {
                    mPlayer.next();
                } else {
                    Toast.makeText(
                            getContext(),"No more videos available",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cardview_action_previous:
                if (mPlayer.hasPrevious()) {
                    mPlayer.previous();
                } else {
                    Toast.makeText(
                            getContext(),"First video reached",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalArgumentException("Undefined button type.");
        }
    }

    private int insertResourcesIntoDatabase(int resourceType) {
        List resources = null;
        Uri insertionUri = null;

        switch (resourceType) {
            case Movie.TRAILER:
                resources = mMovie.getTrailerList();
                insertionUri = MovieProvider.Trailers.withIdTrailers(mMovie.getMovieId());
                if (resources != null) {
                    ContentValues[] contentValuesArray = new ContentValues[resources.size()];
                    for (int i=0;i<resources.size();i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(TrailerColumns.MOVIE_ID, mMovie.getMovieId());
                        contentValues.put(TrailerColumns.VIDEO, (String)resources.get(i));
                        contentValuesArray[i] = contentValues;
                    }
                    return mActivity.getContentResolver().bulkInsert(
                            insertionUri,
                            contentValuesArray
                    );
                }
                break;
            case Movie.REVIEW:
                resources = mMovie.getReviewList();
                insertionUri = MovieProvider.Reviews.withIdReviews(mMovie.getMovieId());
                if (resources != null) {
                    ContentValues[] contentValuesArray = new ContentValues[resources.size()];
                    for (int i=0;i<resources.size();i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MovieTables.ReviewColumns.MOVIE_ID, mMovie.getMovieId());
                        contentValues.put(MovieTables.ReviewColumns.AUTHOR,(String) ((Pair)resources.get(i)).first);
                        contentValues.put(MovieTables.ReviewColumns.CONTENT,(String) ((Pair)resources.get(i)).second);
                        contentValuesArray[i] = contentValues;
                    }
                    return mActivity.getContentResolver().bulkInsert(
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
