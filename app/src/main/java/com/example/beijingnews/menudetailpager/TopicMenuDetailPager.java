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
import com.example.beijingnews.menudetailpager.tabdetailpager.TopicDetailFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 专题详情页面
 */
public class TopicMenuDetailPager extends MenuDetailBasePager {

    @ViewInject(R.id.viewpager)
    private ViewPager2 viewPager;

    @ViewInject(R.id.tablayout)
    private TabLayout tabLayout;

    @ViewInject(R.id.ib_tab_next)
    private ImageButton ibTabNext;

    //页签页面的数据的集合--数据
    private List<NewsCenterPagerBean2.NewsData.ChildrenData> childrenDataList;

    private NewsCenterPagerBean2.NewsData.ChildrenData childrenData;

    public TopicMenuDetailPager(Context context, NewsCenterPagerBean2.NewsData newsData) {
        super(context);
        childrenDataList = newsData.getChildren();
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


        viewPager.setAdapter(new MyTopicMenuDetailPagerAdapter(new TabDetailFragment(childrenData)));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout,viewPager,new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //设置自定义的tablayout的tab
                //tab.setCustomView(getTabView(true));
                tab.setText(childrenDataList.get(position).getTitle());
            }
        });
        mediator.attach();


        viewPager.registerOnPageChangeCallback(new MyOnPageChangeCallback());

    }


    class MyOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            isEnableSlidingMenu(position == 0 );
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyTopicMenuDetailPagerAdapter extends FragmentStateAdapter {


        public MyTopicMenuDetailPagerAdapter(@NonNull Fragment fragment) {
            super((FragmentActivity) context);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new TopicDetailFragment(childrenDataList.get(position));
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
