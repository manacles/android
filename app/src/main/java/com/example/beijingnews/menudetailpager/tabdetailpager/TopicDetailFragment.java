package com.example.beijingnews.menudetailpager.tabdetailpager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.beijingnews.R;
import com.example.beijingnews.base.BaseFragment;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.domain.TabDetailPagerBean;
import com.example.beijingnews.utils.CacheUtils;
import com.example.beijingnews.utils.Constants;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.transformer.AlphaPageTransformer;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * 页签详情页面
 */
public class TopicDetailFragment extends BaseFragment {

    private ViewPager2 viewPager;
    private TextView tvTitle;
    private ListView listView;

    private String url;
    //顶部轮播图部分的数据
    private List<TabDetailPagerBean.DataBean.TopnewsBean> topnews;
    //底部ListView列表的数据
    private List<TabDetailPagerBean.DataBean.NewsBean> news;

    private TopicDetailPagerNewsAdapter adapter;

    private NewsCenterPagerBean2.NewsData.ChildrenData childrenData;

    //下一页的联网路径
    private String moreUrl;
    //是否加载更多
    private boolean isLoadMore = false;
    private PullToRefreshListView mPullRefreshListView;

    private Banner banner;


    public TopicDetailFragment(NewsCenterPagerBean2.NewsData.ChildrenData childrenData) {
        this.childrenData = childrenData;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.topic_detail_pager, null);

        mPullRefreshListView = view.findViewById(R.id.pull_refresh_list);

        listView = mPullRefreshListView.getRefreshableView();

        View topNewsView = View.inflate(context, R.layout.topnews, null);
        banner = topNewsView.findViewById(R.id.banner);


        //把顶部轮播图以头的方式添加到ListView中
        listView.addHeaderView(topNewsView);

        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(context);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        mPullRefreshListView.setOnPullEventListener(soundListener);

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新
                getDataFromNet();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载
                if (TextUtils.isEmpty(moreUrl)) {
                    //没有更多数据
                    Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
//                    listView.onRefreshFinish(false);
                    mPullRefreshListView.onRefreshComplete();
                } else {
                    getMoreDataFromNet();
                }
            }
        });

        return view;
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LogUtil.e("加载更多请求成功==" + result);
                isLoadMore = true;

                //解析数据
                processData(result);
                mPullRefreshListView.onRefreshComplete();
//                listView.onRefreshFinish(false);
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("加载更多请求失败==" + ex.getMessage());
//                listView.onRefreshFinish(false);
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("加载更多请求onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("加载更多请求onFinished");
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + childrenData.getUrl();
        LogUtil.e(childrenData.getTitle() + "的联网地址是==" + url);

        //把缓存数据取出来
        String saveJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        //联网请求数据
        getDataFromNet();

    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CacheUtils.putString(context, url, result);
                LogUtil.e(childrenData.getTitle() + "-页面数据请求成功==" + result);
                processData(result);

                //隐藏下拉刷新控件--重新显示数据，更新时间
//                listView.onRefreshFinish(true);
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求失败==" + ex.getMessage());

                //隐藏下拉刷新控件--不更新时间，只是隐藏
//                listView.onRefreshFinish(false);
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求onFinished");
            }
        });
    }

    private void processData(String json) {
        TabDetailPagerBean bean = parseJson(json);


        if (TextUtils.isEmpty(bean.getData().getMore())) {
            moreUrl = "";
        } else {
            moreUrl = Constants.BASE_URL + bean.getData().getMore();
        }

        //默认和加载更多
        if (!isLoadMore) {
            //默认

            //顶部轮播图的数据
            topnews = bean.getData().getTopnews();

            banner.setPageTransformer(new AlphaPageTransformer());
            banner.setAdapter(new ImageAdapter(topnews))
                    .setLoopTime(3000)
                    .addBannerLifecycleObserver(null)//添加生命周期观察者
                    .setIndicator(new CircleIndicator(context));


            //准备ListView的数据集合
            news = bean.getData().getNews();
            //设置ListView的适配器
            adapter = new TopicDetailPagerNewsAdapter();
            listView.setAdapter(adapter);

        } else {
            //加载更多
            isLoadMore = false;

            //添加到原来的集合中
            news.addAll(bean.getData().getNews());
            //刷新适配器
            adapter.notifyDataSetChanged();

        }

    }




    class TopicDetailPagerNewsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_tabdetail_pager, null);
                viewHolder = new ViewHolder();
                viewHolder.ivListImage = convertView.findViewById(R.id.iv_listimage);
                viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
                viewHolder.tvPubdate = convertView.findViewById(R.id.tv_pubdate);
                viewHolder.line = convertView.findViewById(R.id.line);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到数据
            TabDetailPagerBean.DataBean.NewsBean newsBean = news.get(position);
            String imgUrl = Constants.BASE_URL + newsBean.getListimage();

            /**
             * 两种图片请求方式：xutils3、glide
             */

            //使用xUtils3请求图片
            //x.image().bind(viewHolder.ivListImage, imgUrl,imageOptions);

            //使用Glide请求图片
            //设置图片圆角角度
            RoundedCorners roundedCorners = new RoundedCorners(DensityUtil.dip2px(5));//px
            //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners)
                    .override(DensityUtil.dip2px(90), DensityUtil.dip2px(90));
            Glide.with(context)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.news_pic_default) //异常时候显示的图片
                    .placeholder(R.drawable.news_pic_default) //加载成功前显示的图片
                    .fallback(R.drawable.news_pic_default) //url为空的时候,显示的图片
                    .apply(options)
                    .into(viewHolder.ivListImage);

            viewHolder.tvTitle.setText(newsBean.getTitle());
            viewHolder.tvPubdate.setText(newsBean.getPubdate());
            viewHolder.line.setVisibility(View.GONE);

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivListImage;
        TextView tvTitle;
        TextView tvPubdate;
        View line;
    }

    /**
     * 自定义布局，下面是常见的图片样式，更多实现可以看demo，可以自己随意发挥
     */
    public class ImageAdapter extends BannerAdapter<TabDetailPagerBean.DataBean.TopnewsBean, ImageAdapter.BannerViewHolder> {

        public ImageAdapter(List<TabDetailPagerBean.DataBean.TopnewsBean> mDatas) {
            //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
            super(mDatas);
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

    private TabDetailPagerBean parseJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }
}
