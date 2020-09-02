package com.example.beijingnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.beijingnews.R;
import com.example.beijingnews.domain.TabDetailPagerBean;
import com.example.beijingnews.utils.CacheUtils;
import com.example.beijingnews.utils.Constants;
import com.example.beijingnews.utils.DensityUtil;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.transformer.RotateYTransformer;

import java.util.List;

public class TabDetailAdapter extends RecyclerView.Adapter {

    public static final int TOPNEWS = 0;
    public static final int NEWS = 1;
    public static final int LOADMORE = 2;

    private List<TabDetailPagerBean.DataBean.NewsBean> news;
    private List<TabDetailPagerBean.DataBean.TopnewsBean> topnews;

    private Context context;
    private LayoutInflater inflater;

    private boolean isLoading;

    public TabDetailAdapter(Context context, TabDetailPagerBean.DataBean data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        news = data.getNews();
        topnews = data.getTopnews();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TOPNEWS) {
            return new TopNewsViewHolder(context, inflater.inflate(R.layout.topnews, parent, false));
        } else if (viewType == NEWS) {
            return new NewsViewHolder(context, inflater.inflate(R.layout.item_tabdetail_pager, parent, false));
        } else {
            return new LoadMoreViewHolder(context, inflater.inflate(R.layout.refresh_footer, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TOPNEWS) {
            TopNewsViewHolder topNewsViewHolder = (TopNewsViewHolder) holder;
            topNewsViewHolder.setData(topnews);
        } else if (getItemViewType(position) == NEWS) {
            NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.setData(newsViewHolder, position);
        } else {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            if (isLoading) {
            loadMoreViewHolder.itemView.setVisibility(View.VISIBLE);
            } else {
                loadMoreViewHolder.itemView.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return news.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TOPNEWS;
        } else if (position == news.size() + 1) {
            return LOADMORE;
        } else {
            return NEWS;
        }
    }

    class TopNewsViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private Banner banner;

        public TopNewsViewHolder(Context context, @NonNull View itemView) {
            super(itemView);
            this.context = context;
            this.banner = itemView.findViewById(R.id.banner);
        }

        public void setData(List<TabDetailPagerBean.DataBean.TopnewsBean> topnews) {
            banner.setPageTransformer(new RotateYTransformer());       //设置切换效果
            banner.setAdapter(new MyBannerAdapter(context, topnews))
                    .setLoopTime(3000)
                    .addBannerLifecycleObserver(null)//添加生命周期观察者
                    .setIndicator(new CircleIndicator(context));
        }
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivListImage;
        private TextView tvTitle;
        private TextView tvPubdate;
        private Context context;

        private int currentPosition;

        public NewsViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            ivListImage = itemView.findViewById(R.id.iv_listimage);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPubdate = itemView.findViewById(R.id.tv_pubdate);
        }

        public void setData(NewsViewHolder holder, int position) {
            currentPosition = position - 1;
            //根据位置得到数据
            TabDetailPagerBean.DataBean.NewsBean newsBean = news.get(currentPosition);
            String imgUrl = Constants.BASE_URL + newsBean.getListimage();

            /**
             * 两种图片请求方式：xutils3、glide
             */

            //使用xUtils3请求图片
            //x.image().bind(viewHolder.ivListImage, imgUrl,imageOptions);

            //使用Glide请求图片
            //设置图片圆角角度
            RoundedCorners roundedCorners = new RoundedCorners(DensityUtil.dip2px(context, 5));//px
            //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners)
                    .override(DensityUtil.dip2px(context, 90), DensityUtil.dip2px(context, 90));
            Glide.with(context)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.news_pic_default) //异常时候显示的图片
                    .placeholder(R.drawable.news_pic_default) //加载成功前显示的图片
                    .fallback(R.drawable.news_pic_default) //url为空的时候,显示的图片
                    .apply(options)
                    .into(holder.ivListImage);

            holder.tvTitle.setText(newsBean.getTitle());
            holder.tvPubdate.setText(newsBean.getPubdate());

            //根据是否阅读设置标题颜色
            String idArray = CacheUtils.getString(context, TabDetailFragment.READ_ARRAY_ID);
            if (idArray.contains(newsBean.getId() + "")) {
                holder.tvTitle.setTextColor(Color.GRAY);
            } else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }

            //实现点击效果
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });


        }
    }

    private class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        //LinearLayout llFooterview;

        public LoadMoreViewHolder(Context context, View itemView) {
            super(itemView);
          //  llFooterview = itemView.findViewById(R.id.ll_footerview);
        }
    }


    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }


    //私有属性
    private OnItemClickListener onItemClickListener = null;

    //setter方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //回调接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }


}
