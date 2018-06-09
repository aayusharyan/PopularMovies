package com.aayushsinha.android.movieexplorer;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static RecyclerView mRecyclerView;
    public static final String sharedPreferencesName = "movieExplorer";
    public static final String sortOrder = "sortOrder";
    public static final int sortOrderPopular = 0;
    public static final int sortOrderRating = 1;
    public static final int sortOrderFavourites = 2;
    public static AppDatabase appDatabase;
    private MainViewModel mainViewModel;
    private RequestQueue queue;

    private static int sort;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(sharedPreferencesName, 0);
        SharedPreferences.Editor editor = pref.edit();

        switch (id) {
            case R.id.sort_popular:
                sort = sortOrderPopular;
                break;
            case R.id.sort_rating:
                sort = sortOrderRating;
                break;
            case R.id.show_favourites:
                sort = sortOrderFavourites;
                break;
            default:
                sort = sortOrderPopular;
                break;
        }

        editor.putInt(sortOrder, sort);
        editor.apply();
        setupRV();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(sharedPreferencesName, sortOrderPopular);
        sort = pref.getInt(sortOrder, 0);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("PERMISSION", "Internet Default Permission not Granted");
            finish();
        }

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        appDatabase = mainViewModel.getAppDatabase();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(false);

        int spanCount = MainActivity.calculateNoOfColumns(this);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, spanCount);

        mRecyclerView.setLayoutManager(mLayoutManager);

        queue = Volley.newRequestQueue(this);

        setupRV();
    }

    private void setupRV() {
        ((ProgressBar)findViewById(R.id.loading_list_progressbar)).setVisibility(View.VISIBLE);
        boolean request = true;

        String url = getString(R.string.api_popular_url)+"?api_key="+getString(R.string.api_key_v3)+"&language=en-US&page=1";

        if(sort == sortOrderRating) {
            url = getString(R.string.api_rating_url)+"?api_key="+getString(R.string.api_key_v3)+"&language=en-US&page=1";
        } else if(sort == sortOrderFavourites) {
            request = false;

        }

        if(request) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            MainActivity.APIResponse(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mTextView.setText("That didn't work!");
                    Log.e("VOLLEY", "Error Message");
                }
            });

            queue.add(stringRequest);

            queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    ((ProgressBar)findViewById(R.id.loading_list_progressbar)).setVisibility(View.GONE);
                }
            });
        } else {
            setupData();
            ((ProgressBar)findViewById(R.id.loading_list_progressbar)).setVisibility(View.GONE);
        }
    }

    private void setupData() {

        mainViewModel.getMovies().observe(this, new Observer<SingleMovie[]>() {
            @Override
            public void onChanged(@Nullable SingleMovie[] singleMovies) {
                final JSONArray movies_json = new JSONArray();
                for (SingleMovie movie : singleMovies) {
                    JSONObject singleMovie = new JSONObject();
                    try {
                        singleMovie.put("title", movie.getName());
                        singleMovie.put("poster_path", movie.getPoster_url());
                        singleMovie.put("id", movie.getTmdb_id());
                    } catch (JSONException e) {
                        Log.e("JSON", "Error Creating JSON from DB");
                        e.printStackTrace();
                    }
                    movies_json.put(singleMovie);

                }
                MovieAdapter movieAdapter = new MovieAdapter(movies_json);
                mRecyclerView.setAdapter(movieAdapter);
            }
        });
    }

    private static void APIResponse(String response) {
        try {
            JSONObject res = new JSONObject(response);
            JSONArray movies = res.getJSONArray("results");

            MovieAdapter mAdapter = new MovieAdapter(movies);
            mRecyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }
}
