package com.example.beijingnews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.beijingnews.R;
import com.example.beijingnews.SplashActivity;
import com.example.beijingnews.utils.CacheUtils;
import com.example.beijingnews.utils.DensityUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.btn_start_main)
    Button btnStartMain;
    @BindView(R.id.ll_point_group)
    LinearLayout llPointGroup;
    @BindView(R.id.iv_red_point)
    ImageView ivRedPoint;

    private static final String TAG = "GuideActivity";
    private ArrayList<ImageView> imageViews;
    //两点的间距
    private int leftmax;

    private int widthdpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        //准备数据
        int[] ids = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

        widthdpi = DensityUtil.dip2px(this, 10);
        Log.i(TAG, "onCreate: widthdpi:" + widthdpi);

        imageViews = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            ImageView imageView = new ImageView(this);
            //设置背景
            imageView.setBackgroundResource(ids[i]);
            //添加到集合中
            imageViews.add(imageView);


            //创建点，添加到线性布局里面
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_normal);
            /**
             * 单位是像素
             *  把单位当成dp转成对应像素
             */
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthdpi, widthdpi);
            if (i != 0) {
                //不包括第0个，所有的点距离左边有10个像素
                layoutParams.leftMargin = widthdpi;
            }
            point.setLayoutParams(layoutParams);
            llPointGroup.addView(point);
        }

        //设置ViewPager的适配器
        viewpager.setAdapter(new MyPagerAdapter());

        //根据View的生命周期，当视图执行到onLayout或者onDraw的时候，视图的宽和高，边距都有了
        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());

        //得到屏幕滑动的百分比
        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());
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

    class MyPagerAdapter extends PagerAdapter {

        /**
         * 返回数据的总个数
         */
        @Override
        public int getCount() {
            return imageViews.size();
        }

        /**
         * 作用类似ListView中的 getView
         *
         * @param container ViewPager
         * @param position  要创建页面的位置
         * @return 返回和创建当前页面有关系的值
         */
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = imageViews.get(position);
            //添加到容器中
            viewpager.addView(imageView);
            return imageView;
        }

        /**
         * 判断
         *
         * @param view   当前创建的视图
         * @param object 上面instantiateItem返回的结果值
         * @return
         */
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        /**
         * 销毁页面
         *
         * @param container ViewPager
         * @param position  要销毁页面的位置
         * @param object    要销毁的页面
         */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            //执行不止一次
            ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(MyOnGlobalLayoutListener.this);
            //间距 = 第1个点距离左边的距离 - 第0个点距离左边的距离
            leftmax = llPointGroup.getChildAt(1).getLeft() - llPointGroup.getChildAt(0).getLeft();
            Log.i(TAG, "onGlobalLayout: leftmax:" + leftmax);
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        /**
         * 当页面滚动了会回调这个方法
         *
         * @param position             当前滑动页面的位置
         * @param positionOffset       页面滑动的百分比
         * @param positionOffsetPixels 页面滑动的像素
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            //两点间移动的距离 = 屏幕滑动百分比 * 间距
            int leftmargin = (int) (positionOffset * leftmax);
            Log.i(TAG, "onPageScrolled: position:" + position + " leftmargin:" + leftmargin + " positionOffset:" + positionOffset + " positionOffsetPixels:" + positionOffsetPixels);

            //两点间滑动距离对应的坐标 = 原来的起始位置 +  两点间移动的距离
            leftmargin = position * leftmax + leftmargin;

            //params.leftMargin = 两点间滑动距离对应的坐标
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
            layoutParams.leftMargin = leftmargin;
            ivRedPoint.setLayoutParams(layoutParams);
        }

        /**
         * 当页面被选中的时候回调这个方法
         *
         * @param position 被选中页面的位置
         */
        @Override
        public void onPageSelected(int position) {
            if (position == imageViews.size() - 1) {
                btnStartMain.setVisibility(View.VISIBLE);
            } else {
                btnStartMain.setVisibility(View.GONE);
            }
        }

        /**
         * 当ViewPager页面滑动状态发送变化的时候（3个状态：拖动、惯性、静止）
         *
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
