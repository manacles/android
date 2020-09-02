package com.example.beijingnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beijingnews.R;
import com.example.beijingnews.domain.TabDetailPagerBean;
import com.example.beijingnews.utils.Constants;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * 自定义布局，下面是常见的图片样式，更多实现可以看demo，可以自己随意发挥
 */
public class MyBannerAdapter extends BannerAdapter<TabDetailPagerBean.DataBean.TopnewsBean, MyBannerAdapter.BannerViewHolder> {

    private Context context;

    public MyBannerAdapter(Context context, List<TabDetailPagerBean.DataBean.TopnewsBean> mDatas) {
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        super(mDatas);
        this.context = context;
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topnews_banner, parent, false);

        ImageView imageView = new ImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindView(BannerViewHolder holder, TabDetailPagerBean.DataBean.TopnewsBean data, int position, int size) {
        String imgUrl = Constants.BASE_URL + data.getTopimage();

        holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //图片加载自己实现
        Glide.with(context)
                .load(imgUrl)
                //.apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(holder.imageView);
        holder.textView.setText(data.getTitle());
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public BannerViewHolder(@NonNull View view) {
            super(view);
            this.imageView = view.findViewById(R.id.iv_banner);
            this.textView = view.findViewById(R.id.tv_banner);
        }
    }
}