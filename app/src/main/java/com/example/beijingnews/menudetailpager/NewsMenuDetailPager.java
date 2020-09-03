package com.example.beijingnews.menudetailpager;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beijingnews.R;
import com.example.beijingnews.activity.MainActivity;
import com.example.beijingnews.base.MenuDetailBasePager;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.menudetailpager.tabdetailpager.TabDetailFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.xutils.common.util.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 新闻详情页面
 */
public class NewsMenuDetailPager extends MenuDetailBasePager {

    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.ib_tab_next)
    ImageButton ibTabNext;
    @BindView(R.id.viewpager)
    ViewPager2 viewPager;

    //页签页面的数据的集合--数据
    private List<NewsCenterPagerBean2.NewsData.ChildrenData> childrenDataList;

    public NewsMenuDetailPager(Context context, NewsCenterPagerBean2.NewsData newsData) {
        super(context);
        childrenDataList = newsData.getChildren();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.newsmenu_detail_pager, null);
        ButterKnife.bind(NewsMenuDetailPager.this, view);

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
        LogUtil.e("新闻详情页面的数据初始化了");

        //设置ViewPager的适配器
        viewPager.setAdapter(new MyNewsMenuDetailPagerAdapter(context));
        //设置固定或者滑动
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //设置自定义的tablayout的tab
                //tab.setCustomView(getTabView(true));
                tab.setText(childrenDataList.get(position).getTitle());
            }
        });
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();

        viewPager.registerOnPageChangeCallback(new MyOnPageChangeCallback());

    }

    class MyOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            isEnableSlidingMenu(position == 0);

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state==ViewPager2.SCROLL_STATE_IDLE){

            }
        }
    }

    class MyNewsMenuDetailPagerAdapter extends FragmentStateAdapter {

        public MyNewsMenuDetailPagerAdapter(Context context) {
            super((FragmentActivity) context);

        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new TabDetailFragment(childrenDataList.get(position));
        }

        @Override
        public int getItemCount() {
            return childrenDataList.size();
        }

    }

    /**
     * 根据传入的参数设置是否让drawerlayout可以滑动
     *
     * @param
     */
    private void isEnableSlidingMenu(boolean canSlide) {
        MainActivity mainActivity = (MainActivity) context;
        if (canSlide) {
            mainActivity.getDrawerlayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mainActivity.getDrawerlayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

    }
}
