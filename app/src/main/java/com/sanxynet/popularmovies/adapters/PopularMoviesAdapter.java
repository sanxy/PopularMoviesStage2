package com.sanxynet.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sanxynet.popularmovies.data.model.Movie;
import com.sanxynet.popularmovies.ui.MovieDetailActivity;
import com.sanxynet.popularmovies.utils.PopularMoviesUtilities;
import com.squareup.picasso.Picasso;
import com.sanxynet.popularmovies.R;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {


    private static final int VIEW_TYPE_POSTER_THUMBNAIL_GRID = 0;

    private final Context context;
    private static List<Movie> moviesList;

    public PopularMoviesAdapter(@NonNull Context context, List<Movie> movies) {
        this.context = context;
        moviesList = movies;
    }

    @Override
    public int getItemCount() {
        if (moviesList == null) return 0;
        return moviesList.size();
    }

    @NonNull
    @Override
    public PopularMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutID;

        switch (viewType) {
            case VIEW_TYPE_POSTER_THUMBNAIL_GRID:
                layoutID = R.layout.movie_thumbnail_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        view.setFocusable(true);

        return new PopularMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularMoviesViewHolder holder, int position) {
        // Show movie poster image
        String thumbnailPath = PopularMoviesUtilities.TMDB_API_THUMBNAIL_PATH + moviesList.get(position).getPosterPath();

        Picasso.get()
                .load(thumbnailPath)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_loading_error)
                .into(holder.moviePosterThumbnail);
    }


    public void updateAnswers(List<Movie> movies) {
        moviesList = movies;
        notifyDataSetChanged();
    }

    public static class PopularMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.movie_poster_thumbnail_image)
        ImageView moviePosterThumbnail;

        PopularMoviesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            int position = this.getAdapterPosition();
            // Create the intent to launch the activity to show this movie's details:
            Intent movieDetailIntent = new Intent(context, MovieDetailActivity.class);
            Movie movie = moviesList.get(position);
            // Put the movie into the Extra of the intent:
            movieDetailIntent.putExtra(MovieDetailActivity.PARCELABLE_EXTRA_MOVIE, movie);
            // Launch the activity to show the movie's details:
            context.startActivity(movieDetailIntent);
        }
    }
}
