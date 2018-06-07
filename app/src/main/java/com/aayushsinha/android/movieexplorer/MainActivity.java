package com.aayushsinha.android.movieexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

    public static RecyclerView mRecyclerView;
    public static final String sharedPreferencesName = "movieExplorer";
    public static final String sortOrder = "sortOrder";
    public static final int sortOrderPopular = 0;
    public static final int sortOrderRating = 1;

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
                editor.putInt(sortOrder, sortOrderPopular);
                break;
            case R.id.sort_rating:
                editor.putInt(sortOrder, sortOrderRating);
                break;
            default:
                editor.putInt(sortOrder, sortOrderPopular);
                break;
        }

        Intent intent = getIntent();
        finish();
        startActivity(intent);
        editor.apply();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(sharedPreferencesName, 0);
        int sort = pref.getInt(sortOrder, 0);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("PERMISSION", "Internet Default Permission not Granted");
            finish();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(false);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 4);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = getString(R.string.api_popular_url)+"?api_key="+getString(R.string.api_key_v3)+"&language=en-US&page=1";

        if(sort == sortOrderPopular) {
            url = getString(R.string.api_rating_url)+"?api_key="+getString(R.string.api_key_v3)+"&language=en-US&page=1";
        }


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
    }

    public static void APIResponse(String response) {
        try {
            JSONObject res = new JSONObject(response);
            JSONArray movies = res.getJSONArray("results");

            MovieAdapter mAdapter = new MovieAdapter(movies);
            mRecyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
