package com.example.madelenko.app.moviegami;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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

/**
 * Fragment that displays details for a Movie object. It features a cardview for displaying
 * videos and another one for reviews.
 */
public class MovieDetailFragment extends Fragment {

    private static final String MESSAGE = "message";
    private static final double VIDEO_RATIO = 16.0/9;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        resizeVideoContainer(rootView);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        mActivity.setSupportActionBar(toolbar);

        if (getActivity() instanceof MovieListActivity) {
            rootView.getLayoutParams().width = 600;
        }


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
                        Utility.insertMovieIntoDatabase(mMovie, getContext());
                        Utility.insertResourcesIntoDatabase(Movie.TRAILER, mMovie,getContext());
                        Utility.insertResourcesIntoDatabase(Movie.REVIEW, mMovie, getContext());
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
        setButtonCallbacks(rootView);
        setTrailerButtonCallbacks(rootView);

        return rootView;
    }

    /*
     * Helper method that dynamically sets the height of the YouTubeFragment to respect the
     * 16:9 aspect ratio.
     */
    private void resizeVideoContainer(View rootView) {
        FrameLayout youtubeContainer = (FrameLayout)rootView
                .findViewById(R.id.youtube_fragment_target);

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams)youtubeContainer.getLayoutParams();

        params.height = (int) Math.round(params.width * (1/VIDEO_RATIO));
    }

    /*
     * Defines the actions performed by the buttons in the Review CardView.
     */
    private void setButtonCallbacks(final View rootView) {
        Button buttonShare = (Button)rootView.findViewById(R.id.action_share);
        Button buttonNext = (Button)rootView.findViewById(R.id.action_next);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                String message = String.format(
                        getString(R.string.send_message),mMovie.getOriginalTitle());

                if (mMovie.hasTrailers()) {
                    message += String.format(
                            getString(R.string.trailer_appendix), mMovie.trailerAtPosition(0));
                }
                intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(intent);
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMovie.nextReview();
                bindReviews(rootView);
            }
        });
    }

    /*
     * Helper method used to bind data from the associated movie to the proper views in the
     * hierarchy.
     */
    private void bindViewValues(View rootView) {
        TextView overviewContent = (TextView) rootView.findViewById(R.id.overview_content);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date_textview);
        TextView rating = (TextView) rootView.findViewById(R.id.user_rating_textview);

        overviewContent.setText(mMovie.getSynopsis());
        releaseDate.setText(mMovie.getReleaseDate());
        rating.setText(String.format("%.1f",mMovie.getUserRating()));
        bindReviews(rootView);
    }

    private void bindReviews(View rootView) {
        TextView username = (TextView) rootView.findViewById(R.id.username_textview);
        TextView reviewText = (TextView) rootView.findViewById(R.id.review_body);

        if (mMovie.hasReviews()) {
            username.setText(mMovie.currentReviewAuthor());
            reviewText.setText(mMovie.currentReviewContent());
        }
    }

    /*
     * sets the updateVideoStatus function as a callback for the buttons that control the
     * YouTubePlayer's flow.
     */
    private void setTrailerButtonCallbacks(final View rootView) {
        ImageButton previous = (ImageButton)rootView.findViewById(R.id.cardview_action_previous);
        ImageButton play = (ImageButton)rootView.findViewById(R.id.cardview_action_play);
        ImageButton next = (ImageButton)rootView.findViewById(R.id.cardview_action_next);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVideoStatus(v);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVideoStatus(v);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVideoStatus(v);
            }
        });
    }

    /*
     * Helper method that attaches a YouTubeFragment to a target FrameLayout and
     * defines its style. Supplies a list of videos to be cued by the fragment.
     */
    private void loadYoutubeFragment() {
        YouTubePlayerSupportFragment fragment = new YouTubePlayerSupportFragment();
        fragment.setRetainInstance(true);
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.youtube_fragment_target, fragment)
                .commit();

        fragment.initialize(BuildConfig.YOUTUBE_API_KEY,
                new OnInitializedListener() {
            @Override
            public void onInitializationSuccess(Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                mPlayer = youTubePlayer;
                mPlayer.setPlayerStyle(PlayerStyle.CHROMELESS);
                mPlayer.addFullscreenControlFlag(
                        FULLSCREEN_FLAG_CONTROL_ORIENTATION |
                        FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
                if (mMovie.hasTrailers()){
                    mPlayer.cueVideos(mMovie.getTrailerList());
                }
            }
            @Override
            public void onInitializationFailure(
                    Provider provider,
                    YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    /*
     * Updates the status of the YoutubeFragment associated with this fragment, according to
     * the view that is passed as a parameter, I.e. the button clicked. Functionality has been
     * defined for three buttons: previous, next and play.
     */
    private void updateVideoStatus(View v) {
        int tag = v.getId();
        if (!mMovie.hasTrailers()) {return;}

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
                    ((ImageButton)getView().findViewById(R.id.cardview_action_play))
                            .setImageResource(R.drawable.ic_pause_black_translucid);
                } else {
                    Toast.makeText(
                            getContext(),"No more videos available",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cardview_action_previous:
                if (mPlayer.hasPrevious()) {
                    mPlayer.previous();
                    ((ImageButton)getView().findViewById(R.id.cardview_action_play))
                            .setImageResource(R.drawable.ic_pause_black_translucid);
                } else {
                    Toast.makeText(
                            getContext(),"First video reached",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalArgumentException("Undefined button type.");
        }
    }
}
