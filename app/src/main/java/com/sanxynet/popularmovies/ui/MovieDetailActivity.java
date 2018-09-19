package com.sanxynet.popularmovies.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.sanxynet.popularmovies.snackbar.SnackbarMessage;
import com.sanxynet.popularmovies.adapters.ReviewsAdapter;
import com.sanxynet.popularmovies.adapters.TrailersAdapter;
import com.sanxynet.popularmovies.data.model.Movie;
import com.sanxynet.popularmovies.data.model.ReviewList;
import com.sanxynet.popularmovies.data.model.TrailerList;
import com.sanxynet.popularmovies.db.FavoriteDatabase;
import com.sanxynet.popularmovies.utils.PopularMoviesUtilities;
import com.sanxynet.popularmovies.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;
import com.sanxynet.popularmovies.R;
import java.text.DecimalFormat;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailActivity extends AppCompatActivity {
    public final static String PARCELABLE_EXTRA_MOVIE = "MOVIE";
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private MovieDetailViewModel mMovieDetailViewModel;

    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;

    @BindView(R.id.main_scroll_view)
    ScrollView scrollView;
    @BindView(R.id.movie_details_poster)
    ImageView posterImage;
    @BindView(R.id.movie_details_title)
    TextView titleText;
    @BindView(R.id.movie_details_release_year_label)
    TextView releaseYear;
    @BindView(R.id.movie_details_release_date)
    TextView releaseDate;
    @BindView(R.id.movie_details_user_rating_label)
    TextView userRatingHeader;
    @BindView(R.id.movie_details_user_rating_value)
    TextView userRatingValue;
    @BindView(R.id.movie_details_user_rating_over_label)
    TextView ratingOver;
    @BindView(R.id.movie_details_trailers_label)
    TextView trailerHeader;
    @BindView(R.id.recycler_view_trailers)
    RecyclerView trailers;
    @BindView(R.id.movie_details_reviews_label)
    TextView reviewHeader;
    @BindView(R.id.recycler_view_reviews)
    RecyclerView reviews;
    @BindView(R.id.movie_details_synopsis_label)
    TextView descriptionHeader;
    @BindView(R.id.movie_details_synopsis_value)
    TextView description;
    @BindView(R.id.favorite_star)
    ToggleButton favorite;
    private Snackbar mSnackbar;
    @BindView(R.id.linear_layout)
    LinearLayout linearLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        // Set the ViewModel
        mMovieDetailViewModel = ViewModelProviders.of(this).get(MovieDetailViewModel.class);
        // and observe the LiveData
        // First the movie to be shown
        Log.d(TAG, "observing the ViewModel movie LiveData");
        mMovieDetailViewModel.getMovie().observe(this, movie -> {
            if (movie != null) {
                Log.d(TAG, "a movie has been received");
                updateMovieDetails(movie);
            }
        });
        // Then the trailers for this movie
        Log.d(TAG, "observing the ViewModel TrailerList LiveData");
        mMovieDetailViewModel.getTrailers().observe(this, trailerList -> {
            if (trailerList != null) {
                Log.d(TAG, "a trailer list has been received");
                updateTrailers(trailerList);
            }
        });
        // user reviews of this movie
        Log.d(TAG, "observing the ViewModel ReviewList LiveData");
        mMovieDetailViewModel.getReviews().observe(this, reviewsList -> {
            if (reviewsList != null) {
                Log.d(TAG, "a review list has been received");
                updateReviews(reviewsList);
            }
        });
        // Now the favorite star to show that this movie is in the Favorite Movies DB
        Log.d(TAG, "observing the ViewModel favorite LiveData");
        mMovieDetailViewModel.isMovieInFavorites().observe(this, isInFavorites -> {
            if (isInFavorites != null) {
                Log.d(TAG, "a review list has been received ");
                updateFavoriteStar(isInFavorites);
            }
        });
        // Retrieve the movie instance which details are to be shown in this activity
        Movie movie = getIntent().getParcelableExtra(PARCELABLE_EXTRA_MOVIE);
        if (movie == null) {

            Log.e(TAG, getString(R.string.movie_details_not_available));
            showSnackbar( getString(R.string.movie_details_not_available));
            finish();
        }

        Log.d(TAG, "Opening detailed view for " + Objects.requireNonNull(movie).getTitle());
        // Initialize the UI elements
        initializeUI(movie);

        isMovieInFavorites(movie.getId());

        // Notify the ViewModel about the movie to be shown
        mMovieDetailViewModel.setMovie(movie);
    }

    private void initializeUI(Movie movie) {
        // Set the favorite start to be displayed as designed
        initializeUIFavoriteStar();
        initializeUITrailers(movie);
        initializeUIReviews(movie);
        initializeSnackbar();
    }

    private void initializeSnackbar() {
        mMovieDetailViewModel.getSnackbarMessage().observe(this, (SnackbarMessage.SnackbarObserver)
                snackbarMessageResourceId ->
                        SnackbarUtils.showSnackbar(linearLayout, getString(snackbarMessageResourceId)));
    }

    private void initializeUIFavoriteStar() {
        favorite.setText(null);
        favorite.setTextOn(null);
        favorite.setTextOff(null);
        favorite.setOnClickListener(new FavoriteButtonOnClickListener());
    }

    private void initializeUIReviews(Movie movie) {
        Log.d(TAG, "Setting up the Reviews view");
        // Reviews section
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
       reviews.setLayoutManager(layoutManager);
       reviews.setHasFixedSize(true);
        // Create the adapter
        mReviewsAdapter = new ReviewsAdapter(this, null);
        reviews.setAdapter(mReviewsAdapter);
    }

    private void initializeUITrailers(Movie movie) {
        Log.d(TAG, "Setting up the Trailers view");
        // Trailers section
       LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
       trailers.setLayoutManager(layoutManager);
        trailers.setHasFixedSize(true);
        // Create the adapter
        mTrailersAdapter = new TrailersAdapter(this, null);
        trailers.setAdapter(mTrailersAdapter);
    }

    private void updateTrailers(TrailerList trailerList){
        mTrailersAdapter.updateTrailers(trailerList.getResults());
    }

    private void updateReviews(ReviewList reviewList){
        mReviewsAdapter.updateReviews(reviewList.getResults());
    }

    private void updateFavoriteStar(boolean isFavorite) {
        favorite.setChecked(isFavorite);
    }

    private void updateMovieDetails(Movie movie) {
        // Set the movie title
        titleText.setText(movie.getTitle());
        // Set the movie thumbnail
        String thumbnailPath
                = PopularMoviesUtilities.TMDB_API_THUMBNAIL_PATH
                + movie.getPosterPath();

        Picasso.get()
                .load(thumbnailPath)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_loading_error)
                .into(posterImage);
        // Set the release date
        releaseDate.setText(movie.getReleaseDate());
        // Set the user rating
        userRatingValue.setText(new DecimalFormat(".#").format(movie.getVoteAverage()));
        // Set the overview
        description.setText(movie.getOverview());
    }

    private void isMovieInFavorites(int movieID) {
        Log.d(TAG, "checking if the movie is in the Favorite Movies DB");
        FavoriteDatabase favoriteDatabase = FavoriteDatabase.getInstance(getApplication().getApplicationContext());
        LiveData<Integer> dbMovieId = favoriteDatabase.movieDao().isMovieInFavorites(movieID);
        dbMovieId.observe(this, movieId -> {
            if (movieId != null) {
                Log.d(TAG, "movie is in the favorites list");
                mMovieDetailViewModel.setIsMovieInFavorites(true);
            } else {
                Log.d(TAG, "movie is in not the favorites list");
                mMovieDetailViewModel.setIsMovieInFavorites(false);
            }
        });
    }

    // Set favorite button
    private class FavoriteButtonOnClickListener implements CompoundButton.OnClickListener {
        @Override
        public void onClick(View view) {
            mMovieDetailViewModel.updateMovieInFavorites(
                    favorite.isChecked());
        }
    }

    // Set up snack bar
    private void showSnackbar(String text) {
        mSnackbar = Snackbar.make(linearLayout, text, Snackbar.LENGTH_LONG);
        mSnackbar.setAction(R.string.action_close, view -> mSnackbar.dismiss());
        mSnackbar.show();
    }
}
