package com.sanxynet.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanxynet.popularmovies.R;
import com.sanxynet.popularmovies.data.model.Review;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MovieReviewsViewHolder> {

    private static final String LOG_TAG = ReviewsAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_REVIEWS_GRID = 0;
    private static List<Review> reviewList;
    private final Context context;

    public ReviewsAdapter(@NonNull Context context, List<Review> reviews) {
        this.context = context;
        reviewList = reviews;
    }

    @Override
    public int getItemCount() {
        if (reviewList == null) return 0;
        return reviewList.size();
    }

    @NonNull
    @Override
    public MovieReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutID;

        switch (viewType) {
            case VIEW_TYPE_REVIEWS_GRID:
                layoutID = R.layout.movie_review_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        view.setFocusable(true);

        return new ReviewsAdapter.MovieReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsViewHolder holder, int position) {
        // review header
        holder.reviewHeader.setText(reviewList.get(position).getAuthor());
        // review content
        holder.reviewContent.setText(reviewList.get(position).getContent());

    }

    public void updateReviews(List<Review> reviews) {
        Log.d(LOG_TAG, "Update Review lists");
        reviewList = reviews;
        notifyDataSetChanged();
    }

    public static class MovieReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.review_header)
        TextView reviewHeader;
        @BindView(R.id.review_content)
        TextView reviewContent;

        MovieReviewsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            int position = this.getAdapterPosition();

            // Retrieve the movie from this view's position in the adapter
            Review review = reviewList.get(position);
        }
    }
}
