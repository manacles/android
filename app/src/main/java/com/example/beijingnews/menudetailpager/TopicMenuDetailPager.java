package com.example.beijingnews.menudetailpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.beijingnews.R;
import com.example.beijingnews.activity.MainActivity;
import com.example.beijingnews.base.MenuDetailBasePager;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.menudetailpager.tabdetailpager.TopicDetailPager;
import com.google.android.material.tabs.TabLayout;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题详情页面
 */
public class TopicMenuDetailPager extends MenuDetailBasePager {

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    @ViewInject(R.id.tablayout)
    private TabLayout tabLayout;

    @ViewInject(R.id.ib_tab_next)
    private ImageButton ibTabNext;

    //页签页面的数据的集合--数据
    private List<NewsCenterPagerBean2.NewsData.ChildrenData> childrenData;
    //页签页面的集合--页面
    private ArrayList<TopicDetailPager> tabDetailPagers;

    public TopicMenuDetailPager(Context context, NewsCenterPagerBean2.NewsData newsData) {
        super(context);
        childrenData = newsData.getChildren();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.topicmenu_detail_pager, null);
        x.view().inject(TopicMenuDetailPager.this, view);

        //设置ImageButton的点击事件
        ibTabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("专题详情页面的数据初始化了");

        //准备专题详情页面的数据
        tabDetailPagers = new ArrayList<>();
        for (int i = 0; i < childrenData.size(); i++) {
            tabDetailPagers.add(new TopicDetailPager(context, childrenData.get(i)));
        }

        //设置ViewPager的适配器
        viewPager.setAdapter(new TopicMenuDetailPager.MyNewsMenuDetailPagerAdapter());
        //ViewPager和TabPageIndicator关联
        tabLayout.setupWithViewPager(viewPager);

        //注意以后监听页面的变化，需要用TabPageIndicator来监听
        viewPager.addOnPageChangeListener(new TopicMenuDetailPager.MyOnPageChangeListener());

        //设置固定或者滑动
        //tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        /*自定义TabLayout的样式*/
        /*
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(getTabView(i));
        }
        */
    }

    /*自定义TabLayout的样式*/
    private View getTabView(int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tab, null);
        TextView tv = view.findViewById(R.id.textview);
        ImageView imageView = view.findViewById(R.id.imageview);
        tv.setText(childrenData.get(i).getTitle());
        imageView.setImageResource(R.drawable.arrow_drop_up);
        return view;
    }


    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            isEnableSlidingMenu(position == 0 ? SlidingMenu.TOUCHMODE_FULLSCREEN : SlidingMenu.TOUCHMODE_NONE);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
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
            TopicDetailPager tabDetailPager = tabDetailPagers.get(position);
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

    /**
     * 根据传入的参数设置是否让SlidingMenu可以滑动
     *
     * @param touchmodeFullscreen
     */
    private void isEnableSlidingMenu(int touchmodeFullscreen) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }
}
