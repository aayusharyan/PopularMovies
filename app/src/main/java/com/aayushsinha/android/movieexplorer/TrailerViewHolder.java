package com.aayushsinha.android.movieexplorer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by aayush on 09/06/18.
 */

public class TrailerViewHolder extends RecyclerView.ViewHolder  {
    // each data item is just a string in this case
    View view;
    TextView trailerName;
    ImageView shareImage;

    String trailerURL;
    final Context context;
    TrailerViewHolder(View v) {
        super(v);
        view = v;
        context = v.getContext();
        trailerURL = "";
        trailerName = view.findViewById(R.id.trailerName);
        shareImage = view.findViewById(R.id.shareImage);
    }
}
