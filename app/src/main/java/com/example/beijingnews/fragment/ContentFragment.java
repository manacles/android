package com.example.beijingnews.fragment;

import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.beijingnews.R;
import com.example.beijingnews.activity.MainActivity;
import com.example.beijingnews.adapter.ContentFragmentAdapter;
import com.example.beijingnews.base.BaseFragment;
import com.example.beijingnews.base.BasePager;
import com.example.beijingnews.pager.GovaffairPager;
import com.example.beijingnews.pager.HomePager;
import com.example.beijingnews.pager.NewsCenterPager;
import com.example.beijingnews.pager.SettingPager;
import com.example.beijingnews.pager.SmartServicePager;
import com.example.beijingnews.view.NoScrollViewPager;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

public class ContentFragment extends BaseFragment {
    private static final String TAG = "ContentFragment";

    //2.初始化控件
    @ViewInject(R.id.viewpager)
    private NoScrollViewPager viewpager;
    @ViewInject(R.id.rg_main)
    private RadioGroup rgMain;

    //装五个页面的集合
    private ArrayList<BasePager> basePagers;

    @Override
    public View initView() {
        Log.i(TAG, "initView: 正文Fragment视图被初始化了");

        View view = View.inflate(context, R.layout.content_fragment, null);

        //1.把视图注入到框架中，让ContentFragment.this和view关联起来
        x.view().inject(ContentFragment.this, view);

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.i(TAG, "initData: 正文Fragment数据被初始化了");

        //初始化五个页面，并且放入集合中
        basePagers = new ArrayList<>();
        basePagers.add(new HomePager(context));         //主页面
        basePagers.add(new NewsCenterPager(context));   //新闻中心页面
        basePagers.add(new SmartServicePager(context)); //智慧服务页面
        basePagers.add(new GovaffairPager(context));    //政要指南页面
        basePagers.add(new SettingPager(context));      //设置中心页面

        //设置ViewPager的适配器
        viewpager.setAdapter(new ContentFragmentAdapter(basePagers));

        //设置RadioGroup的选中状态改变的监听
        rgMain.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //监听某个页面被选中，初始对应的页面数据
        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());

        //设置默认选中首页
        rgMain.check(R.id.rb_home);
        basePagers.get(0).initData();
    }

    public NewsCenterPager getNewsCenterPager() {
        return (NewsCenterPager) basePagers.get(1);
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * 当某个页面被选中的时候回调这个方法
         *
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_home:
                    viewpager.setCurrentItem(0, false);
                    isEnableSlidingMenu(false);
                    break;
                case R.id.rb_newscenter:
                    viewpager.setCurrentItem(1, false);
                    isEnableSlidingMenu(true);
                    break;
                case R.id.rb_smartservice:
                    viewpager.setCurrentItem(2, false);
                    isEnableSlidingMenu(false);
                    break;
                case R.id.rb_govaffair:
                    viewpager.setCurrentItem(3, false);
                    isEnableSlidingMenu(false);
                    break;
                case R.id.rb_setting:
                    viewpager.setCurrentItem(4, false);
                    isEnableSlidingMenu(false);
                    break;
            }
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
