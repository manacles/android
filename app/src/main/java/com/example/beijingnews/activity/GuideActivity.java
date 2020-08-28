package com.example.beijingnews.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beijingnews.R;
import com.example.beijingnews.SplashActivity;
import com.example.beijingnews.adapter.GuideViewPagerAdapter;
import com.example.beijingnews.utils.CacheUtils;
import com.example.beijingnews.utils.DensityUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager2 viewpager;
    @BindView(R.id.btn_start_main)
    Button btnStartMain;
    @BindView(R.id.tablayout_guide)
    TabLayout tablayoutGuide;
    @BindView(R.id.ll_tablayout_bg)
    LinearLayout llTablayoutBg;

    private int[] ImageIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        //viewpager2展示的数据
        ImageIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

        //设置ViewPager的适配器
        viewpager.setAdapter(new GuideViewPagerAdapter(this, ImageIds));

        //设置背景：设置三个默认灰点
        for (int i = 0; i < ImageIds.length; i++) {
            llTablayoutBg.addView(getTabView(false));
        }

        //设置固定或者滑动
        tablayoutGuide.setTabMode(TabLayout.MODE_FIXED);

        TabLayoutMediator mediator = new TabLayoutMediator(tablayoutGuide, viewpager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //设置自定义的tablayout的tab
                tab.setCustomView(getTabView(true));
            }
        });

        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
        //tablayout和viewpager2绑定过后，才可以禁用点击
        for (int i = 0; i < tablayoutGuide.getTabCount(); i++) {
            TabLayout.Tab tab = tablayoutGuide.getTabAt(i);
            tab.view.setClickable(false);
        }

        //设置监听，是否显示进入的按钮
        viewpager.registerOnPageChangeCallback(new MyOnPageChangeCallback());

    }

    /*自定义TabLayout的样式*/
    private View getTabView(boolean isTransparent) {
        //-1dp转px
        int oneDp = DensityUtil.dip2px(GuideActivity.this, -1);

        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        ImageView imageView = view.findViewById(R.id.imageview);

        if (isTransparent) {
            //表示是给TabLayout添加自定义布局
            view.setBackgroundColor(Color.TRANSPARENT);
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

    @OnClick(R.id.btn_start_main)
    public void onViewClicked() {
        //1.保存曾经进入过主页面
        CacheUtils.putBoolean(GuideActivity.this, SplashActivity.START_MAIN, true);
        //2.跳转到主页面
        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
        startActivity(intent);
        //3.关闭引导页面
        finish();
    }

    class MyOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == ImageIds.length - 1) {
                btnStartMain.setVisibility(View.VISIBLE);
            } else {
                btnStartMain.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    }
}
