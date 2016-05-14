package com.example.madelenko.app.moviegami;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



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
                    Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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
            setupTrailers(rootView);

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

    private void setupTrailers(View rootView) {

        int size = mMovie.getTrailerList().size();
        for (int i=0; i<size; i++) {
            addTrailerToCard(i);
        }


    }

    private void addTrailerToCard(final int position) {
        TextView textView = new TextView(getContext());
        textView.setText(mActivity.getString(R.string.trailer_text) + (position+1));
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video = (String) mMovie.getTrailerList().get(position);
                mPlayer.loadVideo(video);
            }
        });
        mLinearLayout.addView(textView);
    }




}
