package com.example.madelenko.app.moviegami;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Activity for displaying data related with a specified Movie.
 * It contains a MovieDetailFragment that will provide the Movie related content.
 */
public class MovieDetailActivity extends AppCompatActivity {

    Movie mMovie;
    MovieDetailFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        // If the activity has just been created, we fetch the Movie object from the related intent
        if (savedInstanceState == null) {

            Intent intent = getIntent();
            Movie movie = intent.getParcelableExtra(MovieListActivity.MOVIE);
            Bundle arguments = new Bundle();

            arguments.putParcelable(MovieListActivity.MOVIE, movie);

            mFragment = new MovieDetailFragment();
            mFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, mFragment)    // Attach the fragment to the activity
                    .commit();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
