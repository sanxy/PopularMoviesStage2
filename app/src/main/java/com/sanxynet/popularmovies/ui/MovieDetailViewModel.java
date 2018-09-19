package com.sanxynet.popularmovies.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;

import com.sanxynet.popularmovies.snackbar.SnackbarMessage;
import com.sanxynet.popularmovies.data.model.Movie;
import com.sanxynet.popularmovies.data.model.ReviewList;
import com.sanxynet.popularmovies.data.model.TrailerList;
import com.sanxynet.popularmovies.data.remote.RetrofitClient;
import com.sanxynet.popularmovies.data.remote.RetrofitService;
import com.sanxynet.popularmovies.db.FavoriteDatabase;
import com.sanxynet.popularmovies.db.FavoriteDatabaseExecutors;
import com.sanxynet.popularmovies.R;

import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


@SuppressWarnings("ConstantConditions")
class MovieDetailViewModel extends AndroidViewModel{
    private final String LOG_TAG = MovieDetailViewModel.class.getSimpleName();
    /* MutableLiveData vs LiveData:

    In LiveData - Android Developer Documentation, you can see that for LiveData,
        setValue() & postValue() methods are not public.
    Whereas, in MutableLiveData - Android Developer Documentation, you can see that,
        MutableLiveData extends LiveData internally and also the two magic methods
        of LiveData is publicly available in this and they are setValue() & postValue().

    setValue() : set the value and dispatch the value to all the active
        observers, must be called from main thread.
    postValue() : post a task to main thread to override value set by
        setValue(), must be called from background thread (it can also be called from
        the main/ui thread, even though it is no point in using that call).

    So, LiveData is immutable. MutableLiveData is LiveData which is mutable & thread-safe.
    */
    // The movie to be displayed in the UI
    private final MutableLiveData<Movie> mMovie;
    private final MutableLiveData<TrailerList> mTrailers;
    private final MutableLiveData<ReviewList> mReviews;
    private final MutableLiveData<Boolean> mIsMovieInFavorites;

    private boolean mTrailersQuerySent;
    private boolean mReviewsQuerySent;
    private final SnackbarMessage mSnackbarText = new SnackbarMessage();


    public SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    private void showSnackbarMessage(@StringRes Integer message) {
        mSnackbarText.setValue(message);
    }


    public MovieDetailViewModel(Application application) {
        super(application);
        Log.d(LOG_TAG, "Creating the MovieDetailViewModel");
        mMovie = new MutableLiveData<>();
        mTrailers = new MutableLiveData<>();
        mReviews = new MutableLiveData<>();
        mIsMovieInFavorites = new MutableLiveData<>();
        mTrailersQuerySent = false;
        mReviewsQuerySent = false;
    }

    public MutableLiveData<Movie> getMovie() {
        Log.d(LOG_TAG, "get Movie");
        return mMovie;
    }

    public void setMovie(Movie movie) {

        if (!movie.equals(mMovie.getValue())) {
            int movieId = movie.getId();
            mMovie.postValue(movie);

            // Retrieve movie's trailers
            getMovieTrailers(getApplication().getApplicationContext().getString(R.string.API_KEY_TMDB), movieId);
            // Retrieve movie's reviews
            getMovieReviews(getApplication().getApplicationContext().getString(R.string.API_KEY_TMDB), movieId);

        } else {
            Log.d(LOG_TAG, "the movie is the SAME as current one, nothing needs to be updated");
        }
    }

    public MutableLiveData<Boolean> isMovieInFavorites() {
        Log.d(LOG_TAG, "isMovieInFavorites");
        return mIsMovieInFavorites;
    }

    public void setIsMovieInFavorites(boolean isMovieInFavorites) {
        Log.d(LOG_TAG, "setIsMovieInFavorites");
        mIsMovieInFavorites.postValue(isMovieInFavorites);
    }

    public void updateMovieInFavorites(boolean setFavorite) {
        FavoriteDatabase favoriteDatabase = FavoriteDatabase.getInstance(getApplication().getApplicationContext());
        if (setFavorite) {
            // Add the movie to the favorite movies DB
            if (mIsMovieInFavorites.getValue()) {
                // It is already in the DB. Right now this situation is impossible to happen
                // Update the entry
                Log.d(LOG_TAG, "updating movie entry in the Favorite DB");
                favoriteDatabase.movieDao().updateMovie(mMovie.getValue());
            } else {
                // Add the movie to favorites
                FavoriteDatabaseExecutors.getsInstance().databaseExecutor().execute(() -> {
                    Log.d(LOG_TAG, "adding movie to the Favorite DB");
                    favoriteDatabase.movieDao().insertMovie(mMovie.getValue());

                });
                // Show snack bar
                showSnackbarMessage(R.string.movie_added_favorite);
            }
        } else {
            // Remove the movie from favorites
            Log.d(LOG_TAG, "removing movie from the Favorite DB");
            FavoriteDatabaseExecutors.getsInstance().databaseExecutor().execute(() ->
                    favoriteDatabase.movieDao().deleteMovie(mMovie.getValue()));
            // Show snack bar
            showSnackbarMessage(R.string.movie_removed_favorite);

        }
    }

    public MutableLiveData<TrailerList> getTrailers() {
        Log.d(LOG_TAG, "get Trailers");
        return mTrailers;
    }

    private void setTrailers(TrailerList trailers) {
        Log.d(LOG_TAG, "set Trailers");
        mTrailersQuerySent = true;
        mTrailers.postValue(trailers);
    }

    /**
     * Calls the suitable Retrofit method to get a TrailerList for the given movie.
     *
     * @param movieId Database ID of the movie
     */
    private void getMovieTrailers(String apiKey, int movieId) {

        if (mTrailersQuerySent) {
            Log.d(LOG_TAG, "the trailers were already retrieved before");
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient();
        RetrofitService apiService = retrofit.create(RetrofitService.class);

        Call<TrailerList> call = apiService.getTrailersByMovie(movieId, apiKey);
        call.enqueue(new MovieDetailViewModel.TrailerListCallback());
    }

    public MutableLiveData<ReviewList> getReviews() {
        Log.d(LOG_TAG, "get Reviews");
        return mReviews;
    }

    private void setReviews(ReviewList reviewList) {
        Log.d(LOG_TAG, "set Reviews");
        mReviewsQuerySent = true;
        mReviews.postValue(reviewList);
    }

    /**
     * Calls the suitable Retrofit method to get a ReviewList for the given movie.
     *
     * @param movieId Database ID of the movie
     */
    private void getMovieReviews(String apiKey, int movieId) {

        if (mReviewsQuerySent) {
            Log.d(LOG_TAG, "the reviews were already retrieved before");
            return;
        }

        Log.d(LOG_TAG, "QUERYING to retrieve Reviews from movies with ID " + movieId);

        Retrofit retrofit = RetrofitClient.getClient();
        RetrofitService apiService = retrofit.create(RetrofitService.class);

        Call<ReviewList> call = apiService.getReviewsByMovie(movieId, apiKey);
        call.enqueue(new MovieDetailViewModel.ReviewListCallback());
    }

    private class TrailerListCallback implements Callback<TrailerList> {
        @Override
        public void onResponse(@NonNull Call<TrailerList> call, @NonNull Response<TrailerList> trailerListResponse) {
            if (trailerListResponse.isSuccessful()) {
                Log.d(LOG_TAG, "returned a trailer list");
                setTrailers(trailerListResponse.body());
            } else {
                int statusCode = trailerListResponse.code();
                Log.e(LOG_TAG, trailerListResponse.message());
                setTrailers(null);
            }
        }

        @Override
        public void onFailure(@NonNull Call<TrailerList> call, @NonNull Throwable t) {
            Log.d(LOG_TAG, "the trailer list could not be retrieved");
        }
    }

    private class ReviewListCallback implements Callback<ReviewList> {
        @Override
        public void onResponse(@NonNull Call<ReviewList> call, @NonNull Response<ReviewList> reviewListResponse) {
            if (reviewListResponse.isSuccessful()) {
                Log.d(LOG_TAG, "returned a review list with "
                        + Objects.requireNonNull(reviewListResponse.body()).getTotalResults() + " reviews");
                setReviews(reviewListResponse.body());
            } else {
                int statusCode = reviewListResponse.code();
                Log.e(LOG_TAG, reviewListResponse.message());
                setReviews(null);
            }
        }

        @Override
        public void onFailure(@NonNull Call<ReviewList> call, @NonNull Throwable t) {
            Log.d(LOG_TAG, "the review list could not be retrieved");
        }
    }

}
