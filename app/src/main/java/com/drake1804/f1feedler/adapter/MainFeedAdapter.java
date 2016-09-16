package com.drake1804.f1feedler.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.view.MainActivity;
import com.drake1804.f1feedler.view.custom.NewsFeedView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class MainFeedAdapter extends RecyclerView.Adapter<MainFeedAdapter.ViewHolderMain> {

    private List<NewsFeedModel> newsFeedModels;
    private Context context;
    private int lastPosition = -1;

    public MainFeedAdapter(Context context) {
        this.context = context;
        newsFeedModels = new ArrayList<>();
    }

    @Override
    public ViewHolderMain onCreateViewHolder(ViewGroup parent, int viewType) {
        NewsFeedView newsFeedView;
        if(viewType == 0){
            newsFeedView = new NewsFeedView(context, true);
        } else {
            newsFeedView = new NewsFeedView(context, false);
        }
        DisplayMetrics metrics = new DisplayMetrics();
        ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        newsFeedView.setLayoutParams(new LinearLayoutCompat.LayoutParams(metrics.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolderMain(newsFeedView);
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolderMain holder, int position) {
        holder.newsFeedView.setData(newsFeedModels.get(position));

        /*Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;*/
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

    static class ViewHolderMain extends RecyclerView.ViewHolder {

        NewsFeedView newsFeedView;

        public ViewHolderMain(NewsFeedView itemView) {
            super(itemView);
            newsFeedView = itemView;
        }
    }

}
