package com.aayushsinha.android.movieexplorer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by aayush on 09/06/18.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerViewHolder> {

    JSONArray trailers;

    public TrailerAdapter(JSONArray trailers) {
        this.trailers = trailers;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_layout, parent, false);

        TrailerViewHolder vh = new TrailerViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final TrailerViewHolder holder, int position) {
        try {
            JSONObject singleTrailer = trailers.getJSONObject(position);
            final String trailerName = singleTrailer.optString("name", "");
            holder.trailerName.setText(trailerName);

            String bufferSite = singleTrailer.optString("site", "");

            String trailerURL = "";

            if(Objects.equals(bufferSite, "YouTube")) {
                trailerURL = singleTrailer.optString("key", "");
                if(trailerURL.length() > 0) {
                    trailerURL = holder.context.getString(R.string.youtube_video_url) + "?v=" + trailerURL;
                }
            }

            holder.trailerURL = trailerURL;

            holder.shareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareVideo = new Intent(android.content.Intent.ACTION_SEND);
                    shareVideo.setType("text/plain");
                    shareVideo.putExtra(Intent.EXTRA_SUBJECT, trailerName);
                    shareVideo.putExtra(Intent.EXTRA_TEXT, holder.trailerURL);
                    holder.context.startActivity(Intent.createChooser(shareVideo, holder.context.getString(R.string.share_video_label)));
                }
            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(holder.trailerURL.length() > 0) {
                        try {
                            String url = holder.trailerURL;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            holder.context.startActivity(i);
                        } catch (Exception e) {
                            Toast.makeText(holder.context, R.string.player_error_label, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Non YouTube Trailers cannot be Played
                        Toast.makeText(holder.context, R.string.trailer_error_label, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return trailers.length();
    }
}
