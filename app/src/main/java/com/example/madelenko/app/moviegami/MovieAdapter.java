package com.example.madelenko.app.moviegami;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

final class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Movie[] mMovies;
    private Context mContext;

    public void setMovies(Movie[] movies) {
        this.mMovies = movies;
    }

    MovieAdapter(Context context) { this.mContext = context;}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Movie movie = mMovies[position];
        Picasso.with(MovieListActivity.mInstance)
                .load(movie.getPosterPath())
                .into(holder.mImageView);

        holder.mTitleView.setText(movie.getOriginalTitle());
        holder.mRatingBar.setRating((int) Math.round(movie.getUserRating()/2));
        setStarColor(holder);

//            holder.mImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putString(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//                        MovieDetailFragment fragment = new MovieDetailFragment();
//                        fragment.setArguments(arguments);
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.movie_detail_container, fragment)
//                                .commit();
//                    } else {
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, MovieDetailActivity.class);
//                        intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//
//                        context.startActivity(intent);
//                    }
//                }
//            });
    }

    private void setStarColor(ViewHolder holder) {
        LayerDrawable stars = (LayerDrawable) holder.mRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(
                ContextCompat.getColor(mContext,R.color.translucid_white),
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