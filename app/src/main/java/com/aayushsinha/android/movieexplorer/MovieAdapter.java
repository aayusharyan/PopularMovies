package com.aayushsinha.android.movieexplorer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aayush on 07/06/18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    JSONArray dataSet;

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView mCardView;
        TextView movieName;
        ImageView moviePoster;
        static String poster_url;
        private final Context context;
        ViewHolder(CardView v) {
            super(v);
            mCardView = v;
            context = v.getContext();
            movieName = mCardView.findViewById(R.id.info_text);
            moviePoster = mCardView.findViewById(R.id.poster_image);
            poster_url = context.getString(R.string.api_poster_url);
        }
    }

    public MovieAdapter(JSONArray dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        try {
            JSONObject singleMovie = dataSet.getJSONObject(position);
            String movieName = singleMovie.optString("title", "");
            holder.movieName.setText(movieName);
            String imageURL = singleMovie.optString("poster_path", "");
            if(imageURL.length() > 0) {
                imageURL = ViewHolder.poster_url + imageURL;
            }
            Picasso.get().load(imageURL).into(holder.moviePoster);
            final String movie_id = singleMovie.optString("id", "");
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.context, DetailActivity.class);
                    intent.putExtra("movie_id", movie_id);
                    holder.context.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return dataSet.length();
    }
}
