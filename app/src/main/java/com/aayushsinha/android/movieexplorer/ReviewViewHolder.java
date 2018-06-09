package com.aayushsinha.android.movieexplorer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by aayush on 09/06/18.
 */

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    View view;
    TextView review_name;
    TextView review_content;

    final Context context;
    ReviewViewHolder(View v) {
        super(v);
        view = v;
        context = v.getContext();
        review_name = view.findViewById(R.id.review_name);
        review_content = view.findViewById(R.id.review_content);
    }
}
