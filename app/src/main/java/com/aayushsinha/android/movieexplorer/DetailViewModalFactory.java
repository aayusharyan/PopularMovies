package com.aayushsinha.android.movieexplorer;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by aayush on 09/06/18.
 */

public class DetailViewModalFactory extends ViewModelProvider.NewInstanceFactory {

    private AppDatabase mApplicationDB;
    private String mMovie_id;

    public DetailViewModalFactory(@NonNull AppDatabase appDatabase, String movie_id) {
        mApplicationDB = appDatabase;
        mMovie_id = movie_id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailViewModel(mApplicationDB, mMovie_id);
    }
}
