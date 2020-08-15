package com.example.beijingnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.beijingnews.R;
import com.example.beijingnews.base.MenuDetailBasePager;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.domain.TabDetailPagerBean;
import com.example.beijingnews.utils.CacheUtils;
import com.example.beijingnews.utils.Constants;
import com.example.beijingnews.view.HorizontalScrollViewPager;
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

    private HorizontalScrollViewPager  viewPager;
    private TextView tvTitle;
    private LinearLayout llPointGroup;
    private ListView listView;
    private ImageOptions imageOptions;

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
        llPointGroup = topNewsView.findViewById(R.id.ll_point_group);

        //把顶部轮播图以头的方式添加到ListView中
        listView.addHeaderView(topNewsView);
        return view;
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
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CacheUtils.putString(context, url, result);
                LogUtil.e(childrenData.getTitle() + "-页面数据请求成功==" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求失败==" + ex.getMessage());
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

        LogUtil.e(bean.getData().getNews().get(0).getTitle());

        //顶部轮播图的数据
        topnews = bean.getData().getTopnews();
        //设置ViewPager的适配器
        viewPager.setAdapter(new TabDetailPagerTopNewsAdapter());

        /**
         * 设置 轮播图初始状态
         */
        addPoint();
        tvTitle.setText(topnews.get(prePosition).getTitle());
        viewPager.setCurrentItem(prePosition);

        //监听ViewPager的改变，设置红点变化和文本变化
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        //准备ListView的数据集合
        news = bean.getData().getNews();
        listView.setAdapter(new TabDetailPagerNewsAdapter());
    }

    private void addPoint() {
        llPointGroup.removeAllViews();      //移除所有红点
        for (int i = 0; i < topnews.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.point_selector);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(5), DensityUtil.dip2px(5));

            imageView.setEnabled(i == prePosition);
            if (i != 0) {
                params.leftMargin = DensityUtil.dip2px(8);
            }

            imageView.setLayoutParams(params);
            llPointGroup.addView(imageView);
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //1.设置文本
            tvTitle.setText(topnews.get(position).getTitle());
            //2.对应页面的点高亮
            //把之前的设置为灰色
            llPointGroup.getChildAt(prePosition).setEnabled(false);
            //把当前的设置为红色
            llPointGroup.getChildAt(position).setEnabled(true);

            prePosition = position;

            LogUtil.e("prePosition----》" + prePosition);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

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

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivListImage;
        TextView tvTitle;
        TextView tvPubdate;
    }

    class TabDetailPagerTopNewsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            //设置默认图片
            imageView.setBackgroundResource(R.drawable.home_scroll_default);
            //X轴和Y轴拉伸
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);     //图片拉伸
            //把图片添加到容器中ViewPager
            container.addView(imageView);

            TabDetailPagerBean.DataBean.TopnewsBean topnewsBean = topnews.get(position);
            String imgUrl = Constants.BASE_URL + topnewsBean.getTopimage();

            //联网请求图片
            x.image().bind(imageView, imgUrl);

            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private TabDetailPagerBean parseJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }
}
