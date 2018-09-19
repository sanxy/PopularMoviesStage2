package com.sanxynet.popularmovies.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.sanxynet.popularmovies.R;
import com.sanxynet.popularmovies.adapters.PopularMoviesAdapter;
import com.sanxynet.popularmovies.data.model.Movie;
import com.sanxynet.popularmovies.data.model.MovieList;
import com.sanxynet.popularmovies.data.remote.RetrofitClient;
import com.sanxynet.popularmovies.data.remote.RetrofitService;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import static com.sanxynet.popularmovies.utils.NetworkUtilities.isDeviceConnectedToInternet;


@SuppressWarnings("ConstantConditions")
public class MovieListActivity extends AppCompatActivity {
    private final String TAG = MovieListActivity.class.getSimpleName();

    private final String MOVIE_LIST_KEY = "movie_list";
    private final String SORT_METHOD_KEY = "sort_method";

    private final int SORT_POPULAR = 0;
    private final int SORT_USER_RATING = 1;
    private final int SORT_USER_FAVORITES = 2;

    private PopularMoviesAdapter mPopularMoviesAdapter;
    private MovieList mMovieList;

    @BindView(R.id.recycler_view_movies)
    RecyclerView recyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;
    private Snackbar mSnackbar;
    @BindView(R.id.constraint_layout)
    ConstraintLayout constraintLayout;

    private MovieListViewModel mainViewModel;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovieList != null) {
            outState.putParcelable(MOVIE_LIST_KEY, mMovieList);
        }
        outState.putInt(SORT_METHOD_KEY, mainViewModel.getViewMode().getValue());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        mainViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        // Observe the viewMode
        Log.d(TAG, "observing the MovieListViewModel viewMode LiveData");
        mainViewModel.getViewMode().observe(this, viewMode -> {
            if (viewMode != null) {
                Log.d(TAG, "the viewMode has been received -> updating the UI");
                callMovieAPI(viewMode);
            }
        });
        mainViewModel.getFavoriteMovies().observe(this, favoriteList -> {

            if (mainViewModel.getViewMode().getValue() == SORT_USER_FAVORITES) {
                updateUI(SORT_USER_FAVORITES, favoriteList);
            }
        });

        // Create a layout manager to handle the item views on the RecyclerView
        int DEFAULT_COLUMNS_NUMBER = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, DEFAULT_COLUMNS_NUMBER);

        // set layout manager with the RecyclerView:
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        loadingIndicatorShow();

        mPopularMoviesAdapter = new PopularMoviesAdapter(this, null);
        recyclerView.setAdapter(mPopularMoviesAdapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_LIST_KEY)) {
                Log.d(TAG, "A movie list was found in the savedInstanceState");
                mMovieList = savedInstanceState.getParcelable(MOVIE_LIST_KEY);
                loadingIndicatorHide();
                if (savedInstanceState.getInt(SORT_METHOD_KEY) == SORT_USER_FAVORITES) {
                    updateUI(SORT_USER_FAVORITES, mainViewModel.getFavoriteMovies().getValue());
                } else {
                    mPopularMoviesAdapter.updateAnswers(mMovieList.getResults());
                }
            }
        } else {
            //  gets the same sorting method as when app was closed/destroyed.
            mainViewModel.setViewMode(SORT_POPULAR);
        }
    }

    private void updateUI(int viewMode, List<Movie> movieList) {
        switch (viewMode) {
            case SORT_POPULAR:
                Log.d(TAG, getString(R.string.debug_menu_sort_method_popularity));
                break;
            case SORT_USER_RATING:
                Log.d(TAG, getString(R.string.debug_menu_sort_method_rating));
                break;
            case SORT_USER_FAVORITES:
                Log.d(TAG, getString(R.string.debug_menu_sort_method_user_favorites));
                break;
            default:
                Log.d(TAG, getString(R.string.debug_menu_sort_method_unknown));
                break;
        }
        mPopularMoviesAdapter.updateAnswers(movieList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.menu_sort_popular:
                mainViewModel.setViewMode(SORT_POPULAR);
                callMovieAPI(SORT_POPULAR);
                return true;
            case R.id.menu_sort_rating:
                mainViewModel.setViewMode(SORT_USER_RATING);
                callMovieAPI(SORT_USER_RATING);
                return true;
            case R.id.menu_sort_favorite:
                mainViewModel.setViewMode(SORT_USER_FAVORITES);
                callMovieAPI(SORT_USER_FAVORITES);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Calls the REST API to get data from the server
     *
     * @param callType Type of the call to be done
     */
    private void callMovieAPI(int callType) {
        loadingIndicatorShow();
        // First check if the device is connected to the internet
        if (!isDeviceConnectedToInternet(this)) {

            showSnackbar( getString(R.string.device_not_connected));
        } else {
            switch (callType) {
                case SORT_POPULAR:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_popularity));
                    getSortedMovieList(SORT_POPULAR);
                    break;
                case SORT_USER_RATING:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_rating));
                    getSortedMovieList(SORT_USER_RATING);
                    break;
                case SORT_USER_FAVORITES:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_user_favorites));
                    setupFavoriteMoviesViewModel();
                    break;
                default:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_unknown));
                    break;
            }
        }

    }

    private void setupFavoriteMoviesViewModel() {
        // Favorite Movies list
        MovieListViewModel mainViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        mainViewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                Log.d(TAG, "Updating the favorite movies list from the LiveData in ViewModel");
                loadingIndicatorHide();
                if (mainViewModel.getViewMode().getValue() != SORT_USER_FAVORITES) {
                    mainViewModel.getFavoriteMovies().removeObserver(this);
                    Log.d(TAG, "Sorting method is not user favorites");
                    return;
                }
                if (movies != null) {
                    Log.d(TAG, "The following movies are now on the user's favorite list");
                    for (Movie movie : movies) {
                        Log.d(TAG, movie.getTitle());
                    }
                    updateUI(SORT_USER_FAVORITES, movies);
                } else {
                    Log.d(TAG, "The user's favorite list is empty");
                }
            }
        });
    }

    /**
     * Calls the suitable Retrofit method to get a sorted MovieListActivity depending on the
     * sorting method selected by the user.
     *
     * @param sortingMethod Sorting method selected by the user
     */
    private void getSortedMovieList(int sortingMethod) {
        Log.d(TAG, "Trying to retrieve movies by Rating");

        String apiKey = getString(R.string.API_KEY_TMDB);
        Retrofit retrofit = RetrofitClient.getClient();
        RetrofitService apiService = retrofit.create(RetrofitService.class);

        Call<MovieList> call;
        switch (sortingMethod) {
            default:
            case SORT_POPULAR:
                call = apiService.getMoviesByPopularity(apiKey);
                break;
            case SORT_USER_RATING:
                call = apiService.getMoviesByUserRating(apiKey);
                break;
        }
        call.enqueue(new MovieListCallback());
    }

    /**
     * Hides the loading indicator
     */
    private void loadingIndicatorHide() {
        Log.d(TAG, getString(R.string.loading_indicator_hide));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the loading indicator
     */
    private void loadingIndicatorShow() {
        Log.d(TAG, getString(R.string.loading_indicator_show));
        recyclerView.setVisibility(View.GONE);
       progressBar.setVisibility(View.VISIBLE);
    }

    private class MovieListCallback implements Callback<MovieList> {
        @Override
        public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> movieListResponse) {
            loadingIndicatorHide();
            if (movieListResponse.isSuccessful()) {
                Log.d(TAG, getString(R.string.mlc_onresponse_successful));
                mMovieList = movieListResponse.body();
                // Notify the adapter that we have new data and the activity needs to be updated
                updateUI(mainViewModel.getViewMode().getValue(), mMovieList.getResults());
            } else {
                Log.d(TAG, getString(R.string.mlc_onresponse_failure));
                int statusCode = movieListResponse.code();

                Log.e(TAG, movieListResponse.message());
                showSnackbar( getString(R.string.mlc_onresponse_failure));
            }
        }

        @Override
        public void onFailure(@NonNull Call<MovieList> call, @NonNull Throwable t) {
            loadingIndicatorHide();
            Log.d(TAG, getString(R.string.mlc_onfailure));
            showSnackbar( getString(R.string.mlc_onfailure));
        }
    }

    // Set up snack bar
    private void showSnackbar(String text) {
        mSnackbar = Snackbar.make(constraintLayout, text, Snackbar.LENGTH_LONG);
        mSnackbar.setAction(R.string.action_close, view -> mSnackbar.dismiss());
        mSnackbar.show();
    }
}
