package com.aayushsinha.android.movieexplorer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    private static RecyclerView trailerRecyclerView;
    private static RecyclerView reviewRecyclerView;
    private boolean isFavourite;
    private Menu menu;
    private RequestQueue queue;
    private String movie_id;
    private SingleMovie movieData;
    private boolean executingDB;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        this.menu = menu;
        if(isFavourite) {
            menu.getItem(0).setIcon(R.drawable.favourite_yes);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.favourite) {
            if((movieData == null) || executingDB) {
                Toast.makeText(this, R.string.loading_wait_toast, Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            if(isFavourite) {
                //If Existing is YES, then current after change will be NO
                if(movieData.getTmdb_id().length() != 0) {
                    isFavourite = false;
                    executingDB = true;
                    menu.getItem(0).setIcon(R.drawable.favourite_no);
                    //Time to Delete Data from DB
                    deleteData();
                }
            } else {
                //If Existing is NO, then current after change will be YES
                isFavourite = true;
                executingDB = true;
                menu.getItem(0).setIcon(R.drawable.favourite_yes);
                //Time to Insert Data into DB
                insertData();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                movie_id = null;
            } else {
                movie_id = extras.getString("movie_id");
            }
        } else {
            movie_id = (String) savedInstanceState.getSerializable("movie_id");
        }

        DetailViewModalFactory detailViewModalFactory = new DetailViewModalFactory(MainActivity.appDatabase, movie_id);
        DetailViewModel detailViewModel = ViewModelProviders.of(this, detailViewModalFactory).get(DetailViewModel.class);

        movie_id = detailViewModel.getMovie_id();

        queue = Volley.newRequestQueue(this);

        detailViewModel.getCurrentMovie().observe(this, new Observer<SingleMovie>() {
            @Override
            public void onChanged(@Nullable SingleMovie singleMovie) {
                if(singleMovie == null) {
                    isFavourite = false;
                    if(menu != null) {
                        menu.getItem(0).setIcon(R.drawable.favourite_no);
                    }
                } else {
                    isFavourite = true;
                    if(menu != null) {
                        menu.getItem(0).setIcon(R.drawable.favourite_yes);
                    }

                    ((TextView)findViewById(R.id.movie_name)).setText(singleMovie.getName());

                    ((TextView)findViewById(R.id.movie_release)).setText(singleMovie.getRelease_date());

                    String rating = singleMovie.getRating();
                    if(rating.length() > 0) {
                        rating = "Rating: " + rating + "/10";
                    }
                    ((TextView)findViewById(R.id.movie_rating)).setText(rating);

                    ((TextView)findViewById(R.id.movie_plot_synopsis)).setText(singleMovie.getPlot_synopsis());

                    ((ProgressBar)findViewById(R.id.loading_detail)).setVisibility(View.GONE);
                }
            }
        });

        updateDetail();

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_detail_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDetail();
            }
        });

    }

    private void updateDetail() {
        String url = getString(R.string.api_movie_url)+movie_id+"?api_key="+getString(R.string.api_key_v3);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);

                            String title = res.optString("title", "");
                            ((TextView)findViewById(R.id.movie_name)).setText(title);

                            String release = res.optString("release_date", "");
                            ((TextView)findViewById(R.id.movie_release)).setText(release);

                            String plot_synopsis = res.optString("overview", "");
                            ((TextView)findViewById(R.id.movie_plot_synopsis)).setText(plot_synopsis);

                            String imageURL = res.optString("backdrop_path", "");

                            if(imageURL.length() > 0) {
                                imageURL = getString(R.string.api_backdrop_url) + imageURL;
                            }

                            String poster_path = res.optString("poster_path", "");

                            String rating = res.optString("vote_average", "");

                            movieData = new SingleMovie(movie_id, title, release, poster_path, plot_synopsis, rating);

                            if(rating.length() > 0) {
                                rating = "Rating: " + rating + "/10";
                            }
                            ((TextView)findViewById(R.id.movie_rating)).setText(rating);

                            Picasso.get().load(imageURL).into((ImageView) findViewById(R.id.movie_backdrop));
                            ((ProgressBar)findViewById(R.id.loading_detail)).setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.e("VOLLEY", "Error Message");
            }
        });

        queue.add(stringRequest);


        trailerRecyclerView = (RecyclerView) findViewById(R.id.trailers_rv);
        trailerRecyclerView.setHasFixedSize(true);

        GridLayoutManager trailersGLM = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(trailersGLM);


        String trailer_fetch_url = getString(R.string.api_movie_url)+movie_id+"/videos?api_key="+getString(R.string.api_key_v3);
        StringRequest trailerRequest = new StringRequest(Request.Method.GET, trailer_fetch_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    JSONArray trailers = res.getJSONArray("results");

                    if(trailers.length() > 0) {
                        TrailerAdapter tAdapter = new TrailerAdapter(trailers);
                        trailerRecyclerView.setAdapter(tAdapter);
                        ((TextView)findViewById(R.id.trailers_loading_tv)).setVisibility(View.GONE);
                    } else {
                        ((TextView)findViewById(R.id.trailers_loading_tv)).setText(R.string.no_trailers_message);
                    }
                } catch (JSONException e) {
                    Log.e("JSON", "Error Parsing JOSN Response for Movie Trailers");
                    ((TextView)findViewById(R.id.trailers_loading_tv)).setText(R.string.no_trailers_message);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Error Fetching Movie Trailers");
                ((TextView)findViewById(R.id.trailers_loading_tv)).setText(R.string.error_loading_data_message);
            }
        });


        queue.add(trailerRequest);


        reviewRecyclerView = (RecyclerView) findViewById(R.id.reviews_rv);
        reviewRecyclerView.setHasFixedSize(true);

        LinearLayoutManager reviewsLLM= new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        //LinearLayoutManager reviewsLLM = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewRecyclerView.setLayoutManager(reviewsLLM);

        String reviews_fetch_url = getString(R.string.api_movie_url)+movie_id+"/reviews?api_key="+getString(R.string.api_key_v3);

        StringRequest reviewRequest = new StringRequest(Request.Method.GET, reviews_fetch_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    JSONArray reviews = res.getJSONArray("results");

                    if(reviews.length() > 0) {
                        ReviewAdapter reviewAdapter = new ReviewAdapter(reviews);
                        reviewRecyclerView.setAdapter(reviewAdapter);
                        ((TextView)findViewById(R.id.reviews_loading_tv)).setVisibility(View.GONE);
                    } else {
                        ((TextView)findViewById(R.id.reviews_loading_tv)).setText(R.string.no_review_found_message);
                    }
                } catch (JSONException e) {
                    Log.e("JSON", "Error Parsing JSON Response");
                    ((TextView)findViewById(R.id.reviews_loading_tv)).setText(R.string.no_review_found_message);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((TextView)findViewById(R.id.reviews_loading_tv)).setText(R.string.error_loading_data_message);
            }
        });

        queue.add(reviewRequest);

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                ((SwipeRefreshLayout)findViewById(R.id.swipe_detail_layout)).setRefreshing(false);
            }
        });
    }

    private void insertData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainActivity.appDatabase.singleMovieDao().insert(movieData);
                executingDB = false;
            }
        }).start();
    }

    private void deleteData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainActivity.appDatabase.singleMovieDao().delete(movieData.getTmdb_id());
                executingDB = false;
            }
        }).start();
    }
}
