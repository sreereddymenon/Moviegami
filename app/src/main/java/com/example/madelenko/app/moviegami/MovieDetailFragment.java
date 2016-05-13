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

import com.squareup.picasso.Picasso;


public class MovieDetailFragment extends Fragment {

    private Movie mMovie;
    private AppCompatActivity mActivity;

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
        return rootView;
    }

    private void setupTrailers(View rootView) {
        Log.e(this.getClass().getSimpleName(), "NUMBER OF TRAILERS: " + mMovie.getTrailerList().size());
        //TODO: YOU WERE HERE, TRAILERS GET READ FROM PARCEL. CHECK VIEWS ETC.

        ListViewCompat listview = (ListViewCompat)rootView.findViewById(R.id.trailer_listview);
        int size = mMovie.getTrailerList().size();
        String[] trailers = new String[size];
        for (int i=0; i<size; i++) {
            trailers[i] = "Trailer " + (i+1);
        }
        listview.setAdapter(new ArrayAdapter<String>(
                mActivity,
                R.layout.trailer_textview,
                R.id.trailer_element,
                trailers
        ));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse((String)mMovie.getTrailerList().get(position))
                );
                startActivity(intent);
            }
        });
    }




}
