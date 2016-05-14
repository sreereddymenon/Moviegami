package com.example.madelenko.app.moviegami;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

final class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    public static String TRAILERS_PATH = MovieAdapter.class.getName() + ".Trailers";
    private Movie[] mMovies;
    private MovieListActivity mActivity;

    public void setMovies(Movie[] movies) {
        this.mMovies = movies;
    }

    MovieAdapter(MovieListActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie movie = mMovies[position];
        Picasso.with(mActivity)
                .load(movie.getPosterPath())
                .into(holder.mImageView);

        holder.mTitleView.setText(movie.getOriginalTitle());
        holder.mRatingBar.setRating((int) Math.round(movie.getUserRating()/2));
        setStarColor(holder);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivity.isTwoPaneMode()) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(MovieListActivity.MOVIE,movie);
                    arguments.putStringArrayList("TRAILERS", (ArrayList)movie.getTrailerList());
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    mActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra(MovieListActivity.MOVIE, movie);
                    intent.putStringArrayListExtra(TRAILERS_PATH, (ArrayList)movie.getTrailerList());
                    context.startActivity(intent);
                }
            }
        });
    }

    private void setStarColor(ViewHolder holder) {
        LayerDrawable stars = (LayerDrawable) holder.mRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(
                ContextCompat.getColor(mActivity,R.color.translucid_white),
                PorterDuff.Mode.SRC_ATOP
        );
    }

    @Override
    public int getItemCount() {
        return mMovies==null? 0 : mMovies.length;
    }

    final class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;
        public final TextView mTitleView;
        public final RatingBar mRatingBar;


        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.movie_poster);
            mTitleView = (TextView) view.findViewById(R.id.movie_title);
            mRatingBar = (RatingBar) view.findViewById(R.id.movie_rating);
        }
    }
}