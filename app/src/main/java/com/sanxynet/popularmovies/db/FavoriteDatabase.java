package com.sanxynet.popularmovies.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.sanxynet.popularmovies.data.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class FavoriteDatabase extends RoomDatabase {

    private static final String TAG = FavoriteDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favorite";
    private static FavoriteDatabase sInstance;

    /**
     * Returns an instance of the favorite movies database using the Singleton pattern
     * to ensure that only one database object of the database class is instantiated.
     *
     * @param context
     * @return instance of the database
     */
    @SuppressWarnings("JavaDoc")
    public static FavoriteDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        FavoriteDatabase.class,
                        FavoriteDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Returning the database instance");
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
