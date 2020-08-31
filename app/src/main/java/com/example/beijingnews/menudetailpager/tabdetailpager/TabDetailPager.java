package com.example.beijingnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.beijingnews.R;
import com.example.beijingnews.activity.NewsDetailActivity;
import com.example.beijingnews.base.MenuDetailBasePager;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.domain.TabDetailPagerBean;
import com.example.beijingnews.utils.CacheUtils;
import com.example.beijingnews.utils.Constants;
import com.example.refreshlistview.RefreshListview;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * 页签详情页面
 */
public class TabDetailPager extends MenuDetailBasePager {

    public static final String READ_ARRAY_ID = "read_array_id";
    private ViewPager2 viewPager;
    private TextView tvTitle;
    private RefreshListview listView;
    private ImageOptions imageOptions;
    private TabLayout tablayoutTopnews;
    private LinearLayout llTablayoutBgTopnews;

    /**
     * 之前高亮点的位置
     */
    private int prePosition;

    private final NewsCenterPagerBean2.NewsData.ChildrenData childrenData;
    private String url;
    //顶部轮播图部分的数据
    private List<TabDetailPagerBean.DataBean.TopnewsBean> topnews;
    //底部ListView列表的数据
    private List<TabDetailPagerBean.DataBean.NewsBean> news;

    private TabDetailPagerNewsAdapter adapter;

    //下一页的联网路径
    private String moreUrl;
    //是否加载更多
    private boolean isLoadMore = false;
    private InternalHandler internalHandler;


    public TabDetailPager(Context context, NewsCenterPagerBean2.NewsData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                .setRadius(DensityUtil.dip2px(5))
                .setCrop(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.news_pic_default)
                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        listView = view.findViewById(R.id.listview);

        View topNewsView = View.inflate(context, R.layout.topnews, null);
        viewPager = topNewsView.findViewById(R.id.viewpager);
        tvTitle = topNewsView.findViewById(R.id.tv_title);
        llTablayoutBgTopnews = topNewsView.findViewById(R.id.ll_tablayout_bg_topnews);
        tablayoutTopnews = topNewsView.findViewById(R.id.tablayout_topnews);


        //把顶部轮播图以头的方式添加到ListView中
        //listView.addHeaderView(topNewsView);

        listView.addTopNewsView(topNewsView);


        //设置监听下拉刷新和加载更多
        listView.setOnRefreshListener(new RefreshListview.OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                getDataFromNet();
            }

            @Override
            public void onLoadMore() {
                if (TextUtils.isEmpty(moreUrl)) {
                    //没有更多数据
                    Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
                    listView.onRefreshFinish(false);
                } else {
                    getMoreDataFromNet();
                }
            }
        });

        //设置ListView的item的监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int realPosition = position - 1;
                TabDetailPagerBean.DataBean.NewsBean newsBean = news.get(realPosition);
//                Toast.makeText(context, "newsBean:id==" + newsBean.getId() + "title:" + newsBean.getTitle(), Toast.LENGTH_SHORT).show();
                LogUtil.e("newsBean:id==" + newsBean.getId() + "title:" + newsBean.getTitle() + ",url=" + newsBean.getUrl());

                //1.取出保存的Id集合
                String idArray = CacheUtils.getString(context, READ_ARRAY_ID);
                //2.判断是否存在，如果不存在，才保存，并且刷新适配器
                if (!idArray.contains(newsBean.getId() + "")) {
                    CacheUtils.putString(context, READ_ARRAY_ID, idArray + newsBean.getId() + ",");

                    //刷新适配器
                    adapter.notifyDataSetChanged();
                }

                //跳转到新闻浏览页面
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("url", Constants.BASE_URL + newsBean.getUrl());
                context.startActivity(intent);
            }
        });

        return view;
    }

    /*自定义TabLayout的样式*/
    private View getTabView(boolean isTransparent) {
        //-1dp转px
        int oneDp = com.example.beijingnews.utils.DensityUtil.dip2px(context, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.item_tab, null);
        ImageView imageView = view.findViewById(R.id.imageview);

        if (isTransparent) {
            //表示是给TabLayout添加自定义布局
            view.setBackgroundColor(Color.TRANSPARENT);
            // imageView.set
            imageView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            //表示给背景添加默认灰点视图
            //因为自定义tablayout的视图想要填充tab,设置了  app:tabPaddingEnd="-1dp"
            //                                          app:tabPaddingStart="-1dp"
            //这里作为背景也设置个填充值，以保证位置一致
            view.setPadding(oneDp, 0, oneDp, 0);
            imageView.setBackgroundResource(R.drawable.point_normal);
        }

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

                listView.onRefreshFinish(false);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("加载更多请求失败==" + ex.getMessage());
                listView.onRefreshFinish(false);
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

        //添加tablayout指示器
        addPoint();
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
                listView.onRefreshFinish(true);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求失败==" + ex.getMessage());

                //隐藏下拉刷新控件--不更新时间，只是隐藏
                listView.onRefreshFinish(false);
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
            //设置ViewPager的适配器
            viewPager.setAdapter(new TabDetailPagerTopNewsAdapter());

            tvTitle.setText(topnews.get(prePosition).getTitle());
            viewPager.setCurrentItem(prePosition);

            //监听ViewPager的改变，设置红点变化和文本变化
            viewPager.registerOnPageChangeCallback(new MyOnPageChangeCallback());

            //准备ListView的数据集合
            news = bean.getData().getNews();
            //设置ListView的适配器
            adapter = new TabDetailPagerNewsAdapter();
            listView.setAdapter(adapter);

        } else {
            //加载更多
            isLoadMore = false;

            //添加到原来的集合中
            news.addAll(bean.getData().getNews());
            //刷新适配器
            adapter.notifyDataSetChanged();

        }

        //发消息，每隔4000切换一次ViewPager页面
        if (internalHandler == null) {
            internalHandler = new InternalHandler();
        }
        //是把消息队列所有的消息和回调
        internalHandler.removeCallbacksAndMessages(null);

        internalHandler.postDelayed(new MyRunnable(), 4000);

    }

    private void addPoint() {
        llTablayoutBgTopnews.removeAllViews();
        //设置背景：设置默认灰点
        for (int i = 0; i < topnews.size(); i++) {
            llTablayoutBgTopnews.addView(getTabView(false));
        }
        //设置固定或者滑动
        tablayoutTopnews.setTabMode(TabLayout.MODE_FIXED);
        TabLayoutMediator mediator = new TabLayoutMediator(tablayoutTopnews, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //设置自定义的tablayout的tab
                tab.setCustomView(getTabView(true));
            }
        });
        mediator.attach();

        //tablayout和viewpager2绑定过后，才可以禁用点击
        for (int i = 0; i < tablayoutTopnews.getTabCount(); i++) {
            TabLayout.Tab tab = tablayoutTopnews.getTabAt(i);
            tab.view.setClickable(false);
        }
    }

    class InternalHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //切换到ViewPager的下一个页面
            viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % topnews.size());

            internalHandler.postDelayed(new MyRunnable(), 4000);
        }
    }

    class MyRunnable implements Runnable {
        @Override
        public void run() {
            internalHandler.sendEmptyMessage(0);
        }
    }

    class MyOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            tvTitle.setText(topnews.get(position).getTitle());

            prePosition = position;
        }

        private boolean isDragging = false;

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {    //拖拽
                isDragging = true;
                //拖拽要移除消息
                internalHandler.removeCallbacksAndMessages(null);
            } else if (state == ViewPager.SCROLL_STATE_SETTLING && isDragging) {  //惯性
                //发消息
                isDragging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(), 4000);
            } else if (state == ViewPager.SCROLL_STATE_IDLE && isDragging) {  //静止
                //发消息
                isDragging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(), 4000);
            }
        }
    }


    class TabDetailPagerNewsAdapter extends BaseAdapter {

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

            //根据是否阅读设置标题颜色
            String idArray = CacheUtils.getString(context, READ_ARRAY_ID);
            if (idArray.contains(newsBean.getId() + "")) {
                viewHolder.tvTitle.setTextColor(Color.GRAY);
            } else {
                viewHolder.tvTitle.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivListImage;
        TextView tvTitle;
        TextView tvPubdate;
    }

    class TabDetailPagerTopNewsAdapter extends RecyclerView.Adapter<TabDetailPagerTopNewsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_viewpager_guide, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            //设置默认图片
            holder.imageView.setBackgroundResource(R.drawable.home_scroll_default);
            //X轴和Y轴拉伸
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);     //图片拉伸

            TabDetailPagerBean.DataBean.TopnewsBean topnewsBean = topnews.get(position);
            String imgUrl = Constants.BASE_URL + topnewsBean.getTopimage();

            //联网请求图片
            x.image().bind(holder.imageView, imgUrl);

            holder.imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //是把消息队列所有的消息和回调
                            internalHandler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP:
                            internalHandler.removeCallbacksAndMessages(null);
                            internalHandler.postDelayed(new MyRunnable(), 4000);
                            break;
                    }
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return topnews.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.iv_item);
            }
        }
    }

    private TabDetailPagerBean parseJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }
}
