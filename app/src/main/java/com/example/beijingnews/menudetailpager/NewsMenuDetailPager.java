package com.example.beijingnews.menudetailpager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.beijingnews.R;
import com.example.beijingnews.base.MenuDetailBasePager;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.menudetailpager.tabdetailpager.TabDetailPager;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻详情页面
 */
public class NewsMenuDetailPager extends MenuDetailBasePager {

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    @ViewInject(R.id.tabPageIndicator)
    private TabPageIndicator tabPageIndicator;

    //页签页面的数据的集合--数据
    private List<NewsCenterPagerBean2.NewsData.ChildrenData> childrenData;
    //页签页面的集合--页面
    private ArrayList<TabDetailPager> tabDetailPagers;

    public NewsMenuDetailPager(Context context, NewsCenterPagerBean2.NewsData newsData) {
        super(context);
        childrenData = newsData.getChildren();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.newsmenu_detail_pager, null);
        x.view().inject(NewsMenuDetailPager.this, view);
        //tabPageIndicator.set
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("新闻详情页面的数据初始化了");

        //准备新闻详情页面的数据
        tabDetailPagers = new ArrayList<>();
        for (int i = 0; i < childrenData.size(); i++) {
            tabDetailPagers.add(new TabDetailPager(context, childrenData.get(i)));
        }

        //设置ViewPager的适配器
        viewPager.setAdapter(new MyNewsMenuDetailPagerAdapter());
        //ViewPager和TabPageIndicator关联
        tabPageIndicator.setViewPager(viewPager);

        //注意以后监听页面的变化，需要用TabPageIndicator来监听


    }

    class MyNewsMenuDetailPagerAdapter extends PagerAdapter {

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return childrenData.get(position).getTitle();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TabDetailPager tabDetailPager = tabDetailPagers.get(position);
            View rootView = tabDetailPager.rootView;
            tabDetailPager.initData();      //初始化数据
            container.addView(rootView);
            return rootView;
        }

        @Override
        public int getCount() {
            return tabDetailPagers.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
