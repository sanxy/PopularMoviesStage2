package com.sanxynet.popularmovies.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanxynet.popularmovies.data.model.Trailer;
import com.sanxynet.popularmovies.utils.PopularMoviesUtilities;
import com.squareup.picasso.Picasso;
import com.sanxynet.popularmovies.R;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.MovieTrailersViewHolder> {


    private static final String LOG_TAG = TrailersAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_TRAILERS_THUMBNAIL_GRID = 0;
    private static List<Trailer> trailerList;
    private final Context context;

    public TrailersAdapter(@NonNull Context context, List<Trailer> trailers) {
        this.context = context;
        trailerList = trailers;
    }

    @Override
    public int getItemCount() {
        if (trailerList == null) return 0;
        return trailerList.size();
    }

    @NonNull
    @Override
    public MovieTrailersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutID;

        switch (viewType) {
            case VIEW_TYPE_TRAILERS_THUMBNAIL_GRID:
                layoutID = R.layout.movie_trailer_thumbnail_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        view.setFocusable(true);

        return new TrailersAdapter.MovieTrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailersViewHolder holder, int position) {
        // movie title
        holder.trailerID.setText(trailerList.get(position).getName());
        // Show movie poster image
        String thumbnailPath = "http://img.youtube.com/vi/" + trailerList.get(position).getKey() + "/0.jpg";

        Picasso.get()
                .load(thumbnailPath)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_loading_error)
                .into(holder.trailerPosterThumbnail);
    }

    public void updateTrailers(List<Trailer> trailers) {
        Log.d(LOG_TAG, "Update trailer lists");
        trailerList = trailers;
        notifyDataSetChanged();
    }

    public static class MovieTrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.movie_trailer_thumbnail_image)
        ImageView trailerPosterThumbnail;
        @BindView(R.id.movie_trailer_thumbnail_title)
        TextView trailerID;

        MovieTrailersViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            int position = this.getAdapterPosition();

            // Retrieve the movie from this view's position in the adapter
            Trailer trailer = trailerList.get(position);

            try {
                PopularMoviesUtilities.launchTrailerVideoInYoutubeApp(context, trailer.getKey());
            } catch (ActivityNotFoundException exception) {
                PopularMoviesUtilities.launchTrailerVideoInYoutubeBrowser(context, trailer.getKey());
            }
        }
    }
}
