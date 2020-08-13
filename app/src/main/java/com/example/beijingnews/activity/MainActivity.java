package com.example.beijingnews.activity;

import android.os.Bundle;

import com.example.beijingnews.R;
import com.example.beijingnews.fragment.ContentFragment;
import com.example.beijingnews.fragment.LeftmenuFragment;
import com.example.beijingnews.utils.DensityUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

    public static final String MAIN_CONTENT_TAG = "main_content_tag";
    public static final String LEFTMENU_TAG = "leftmenu_tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1.设置主页面
        setContentView(R.layout.activity_main);

        //2.设置左侧菜单
        setBehindContentView(R.layout.activity_leftmenu);

        //3.设置右侧菜单
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setSecondaryMenu(R.layout.activity_rightmenu); //设置右侧菜单

        //4.设置显示的模式：左侧菜单+主页
        slidingMenu.setMode(SlidingMenu.LEFT);

        //5.设置滑动模式：滑动边缘、全屏滑动、不可以滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        //6.设置主页占据的宽度
        slidingMenu.setBehindOffset(DensityUtil.dip2px(MainActivity.this, 200));


        //初始化Fragment
        initFragment();
    }

    private void initFragment() {
       /* //1.得到FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //3.替换
        transaction.replace(R.id.fl_main_content, new ContentFragment(), MAIN_CONTENT_TAG);  //主页
        transaction.replace(R.id.fl_leftmenu, new LeftmenuFragment(), LEFTMENU_TAG);  //主页
        //4.提交
        transaction.commit();

*/
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main_content, new ContentFragment(), MAIN_CONTENT_TAG)
                .replace(R.id.fl_leftmenu, new LeftmenuFragment(), LEFTMENU_TAG)
                .commit();
    }
}
