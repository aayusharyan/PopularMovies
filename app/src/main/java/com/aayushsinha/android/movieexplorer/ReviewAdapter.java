package com.aayushsinha.android.movieexplorer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aayush on 09/06/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
    private JSONArray reviews;

    ReviewAdapter(JSONArray reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_layout, parent, false);

        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        try {
            JSONObject singleReview = reviews.getJSONObject(position);
            String reviewName = singleReview.optString("author", "");
            holder.review_name.setText(reviewName);

            String reviewContent = singleReview.optString("content", "");
            holder.review_content.setText(reviewContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviews.length();
    }
}
