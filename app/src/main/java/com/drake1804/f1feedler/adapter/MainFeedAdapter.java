package com.drake1804.f1feedler.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.utils.AppUtils;
import com.drake1804.f1feedler.view.MainActivity;
import com.drake1804.f1feedler.view.custom.NewsFeedView;
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
        NewsFeedView newsFeedView = new NewsFeedView(context);
        DisplayMetrics metrics = new DisplayMetrics();
        ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        newsFeedView.setLayoutParams(new LinearLayoutCompat.LayoutParams(metrics.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(newsFeedView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.newsFeedView.setData(newsFeedModels.get(position));
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

        NewsFeedView newsFeedView;

        public ViewHolder(NewsFeedView itemView) {
            super(itemView);
            newsFeedView = itemView;
        }
    }

}
