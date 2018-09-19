package com.sanxynet.popularmovies.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import com.sanxynet.popularmovies.data.model.Movie;
import com.sanxynet.popularmovies.db.FavoriteDatabase;

import java.util.List;

class MovieListViewModel extends AndroidViewModel {
    private static final String TAG = MovieListViewModel.class.getSimpleName();

    private final LiveData<List<Movie>> mFavoriteMovies;

    private final MutableLiveData<Integer> mViewMode;

    public MutableLiveData<Integer> getViewMode() {
        return mViewMode;
    }

    public void setViewMode(int viewMode) {
        mViewMode.postValue(viewMode);
    }

    public MovieListViewModel(@NonNull Application application) {
        super(application);
        FavoriteDatabase favoriteDatabase
                = FavoriteDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the favorite movies from the database");
        mFavoriteMovies = favoriteDatabase.movieDao().getAllFavoriteMovies();

        mViewMode = new MutableLiveData<>();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mFavoriteMovies;
    }
}
