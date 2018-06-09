package com.aayushsinha.android.movieexplorer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by aayush on 08/06/18.
 */

@Entity(tableName = "movies", indices = {@Index(value = {"tmdb_id"}, unique = true)})
public class SingleMovie {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "tmdb_id") @NonNull
    private String tmdb_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "release_date")
    private String release_date;

    @ColumnInfo(name = "poster_url")
    private String poster_url;

    @ColumnInfo(name = "plot_synopsis")
    private String plot_synopsis;

    @ColumnInfo(name = "rating")
    private String rating;

    @Ignore
    public SingleMovie(String tmdb_id, String name, String release_date, String poster_url, String plot_synopsis, String rating) {
        this.id = 0;
        this.tmdb_id = tmdb_id;
        this.name = name;
        this.release_date = release_date;
        this.poster_url = poster_url;
        this.plot_synopsis = plot_synopsis;
        this.rating = rating;
    }

    public SingleMovie(int id, String tmdb_id, String name, String release_date, String poster_url, String plot_synopsis, String rating) {
        this.id = id;
        this.tmdb_id = tmdb_id;
        this.name = name;
        this.release_date = release_date;
        this.poster_url = poster_url;
        this.plot_synopsis = plot_synopsis;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTmdb_id() {
        return tmdb_id;
    }

    public void setTmdb_id(String tmdb_id) {
        this.tmdb_id = tmdb_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getPlot_synopsis() {
        return plot_synopsis;
    }

    public void setPlot_synopsis(String plot_synopsis) {
        this.plot_synopsis = plot_synopsis;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
