package com.example.beijingnews.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GuideViewPagerAdapter extends RecyclerView.Adapter {

    private Context context;
    private int[] imgIds;

    public GuideViewPagerAdapter(Context context, int[] imgIds) {
        this.context = context;
        this.imgIds = imgIds;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ViewHolder holder = new ViewHolder(imageView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setBackgroundResource(imgIds[position]);
    }


    @Override
    public int getItemCount() {
        return imgIds.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull ImageView imageView) {
            super(imageView);
        }
    }
}
