package com.drake1804.f1feedler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class MainFeedAdapter extends RecyclerView.Adapter<MainFeedAdapter.ViewHolder> {

    private List<NewsFeedModel> newsFeedModels;
    private Context context;

    public MainFeedAdapter(Context context) {
        this.context = context;
        newsFeedModels = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_feed_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context)
                .load(newsFeedModels.get(position).getImageUrl())
                .placeholder(R.drawable.holder)
                .into(holder.image);
        holder.title.setText(newsFeedModels.get(position).getTitle());
        holder.description.setText(newsFeedModels.get(position).getDescription());
        holder.date.setText(/*DateTimeUtils.stringToDate(*/newsFeedModels.get(position).getCreatingDate()/*).toString()*/);
    }

    @Override
    public int getItemCount() {
        return newsFeedModels.size();
    }

    public List<NewsFeedModel> getNewsFeedModels() {
        return newsFeedModels;
    }

    public void setData(List<NewsFeedModel> newsFeedModels) {
        this.newsFeedModels.clear();
        this.newsFeedModels.addAll(newsFeedModels);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image)
        CircleImageView image;

        @Bind(R.id.title)
        TextView title;

        @Bind(R.id.description)
        TextView description;

        @Bind(R.id.date)
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
