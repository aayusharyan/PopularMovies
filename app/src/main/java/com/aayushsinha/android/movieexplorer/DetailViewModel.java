package com.aayushsinha.android.movieexplorer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

/**
 * Created by aayush on 09/06/18.
 */

public class DetailViewModel extends ViewModel {
    private String movie_id;

    private LiveData<SingleMovie> currentMovie;

    //Here, Param is the movie_id
    public DetailViewModel(@NonNull AppDatabase appDatabase, String mMovie_Id) {

        currentMovie = appDatabase.singleMovieDao().fetch(mMovie_Id);
        movie_id = mMovie_Id;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public LiveData<SingleMovie> getCurrentMovie() {
        return currentMovie;
    }
}
