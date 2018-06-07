package com.aayushsinha.android.movieexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String movie_id;
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

        RequestQueue queue = Volley.newRequestQueue(this);

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

                            String rating = res.optString("vote_average", "");
                            if(rating.length() > 0) {
                                rating = "Rating: " + rating + "/10";
                            }
                            ((TextView)findViewById(R.id.movie_rating)).setText(rating);

                            String plot_synopsis = res.optString("overview", "");
                            ((TextView)findViewById(R.id.movie_plot_synopsis)).setText(plot_synopsis);

                            String imageURL = res.optString("backdrop_path", "");
                            if(imageURL.length() > 0) {
                                imageURL = getString(R.string.api_backdrop_url) + imageURL;
                            }
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

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
