package com.sanxynet.popularmovies.db;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FavoriteDatabaseExecutors {

    private static FavoriteDatabaseExecutors sInstance;
    private static final Object LOCK = new Object();
    private final Executor databaseExecutor;

    private FavoriteDatabaseExecutors(Executor databaseExecutor) {
        this.databaseExecutor = databaseExecutor;
    }

    public static FavoriteDatabaseExecutors getsInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FavoriteDatabaseExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor databaseExecutor() {
        return databaseExecutor;
    }
}
