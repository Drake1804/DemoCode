package com.drake1804.f1feedler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.model.CommentModel;
import com.drake1804.f1feedler.utils.TimeAgo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pavel.Shkaran on 5/30/2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>  {

    private List<CommentModel> commentModels;
    private Context context;


    public CommentsAdapter(Context context) {
        this.context = context;
        commentModels = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(commentModels.get(position).getUserName());
        Picasso.with(context).load(commentModels.get(position).getUserImage()).into(holder.image);
        holder.message.setText(commentModels.get(position).getMessage());
        holder.date.setText(TimeAgo.toDuration(commentModels.get(position).getTime().getTime()));
    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    public void setCommentModels(List<CommentModel> commentModels) {
        this.commentModels.clear();
        this.commentModels.addAll(commentModels);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.image)
        CircleImageView image;

        @BindView(R.id.message)
        TextView message;

        @BindView(R.id.date)
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
