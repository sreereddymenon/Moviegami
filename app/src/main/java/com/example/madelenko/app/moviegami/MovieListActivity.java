package com.example.madelenko.app.moviegami;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieListActivity extends AppCompatActivity {

    public static final String POPULAR = "popular";
    public static final String TOP = "top";
    public static final String FAVORITES = "favorites";
    public static final String MOVIE = "movie";
    public static final String QUERY_TYPE = "query type";

    private RecyclerView mRecyclerView;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    public boolean isTwoPaneMode() {
        return mTwoPane;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        setupTabLayout();

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout.addTab(tabLayout.newTab().setText(POPULAR));
        tabLayout.addTab(tabLayout.newTab().setText(TOP));
        tabLayout.addTab(tabLayout.newTab().setText(FAVORITES));

        List<Fragment> fragments = new ArrayList<>();
        for (int i=0;i<3;i++) {
            Bundle bundle = new Bundle();
            bundle.putInt(QUERY_TYPE, i);
            fragments.add(ListFragment
                    .instantiate(this,ListFragment.class.getName(),bundle));
        }
        viewPager.setAdapter(new MoviePagerAdapter(getSupportFragmentManager(), fragments));
        tabLayout.setupWithViewPager(viewPager);
    }
}