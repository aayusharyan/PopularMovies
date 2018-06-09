package com.aayushsinha.android.movieexplorer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

/**
 * Created by aayush on 09/06/18.
 */

public class MainViewModel extends AndroidViewModel {

    private LiveData<SingleMovie[]> movies;

    private AppDatabase appDatabase;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(this.getApplication());
        appDatabase = db;
        movies = db.singleMovieDao().fetchAll();
    }

    public LiveData<SingleMovie[]> getMovies() {
        return movies;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
