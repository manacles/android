package com.example.beijingnews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beijingnews.R;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_viewpager_guide, parent, false);
        ViewHolder holder = new ViewHolder(view);
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
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item);
        }
    }
}
