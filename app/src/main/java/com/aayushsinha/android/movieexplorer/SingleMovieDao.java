package com.aayushsinha.android.movieexplorer;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by aayush on 09/06/18.
 */

@Dao
public interface SingleMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SingleMovie... singleMovies);

    @Query("SELECT * FROM movies")
    LiveData<SingleMovie[]> fetchAll();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(SingleMovie singleMovie);

    @Query("SELECT * FROM movies WHERE tmdb_id = :tmdb_id")
    LiveData<SingleMovie> fetch(String tmdb_id);

    @Query("DELETE FROM movies WHERE tmdb_id = :tmdb_id")
    void delete(String tmdb_id);
}
