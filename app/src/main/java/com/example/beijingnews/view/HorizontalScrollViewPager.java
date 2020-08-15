package com.example.beijingnews.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 水平方法滑动的ViewPager
 */

public class HorizontalScrollViewPager extends ViewPager {

    /**
     * 起始坐标
     */
    private float startX;
    private float startY;

    public HorizontalScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public HorizontalScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //要求父层视图不拦截事件当前控件的事件
                getParent().requestDisallowInterceptTouchEvent(true);   //都把事件传给当前控件（HorizontalScrollViewPager）
                //1.记录起始坐标
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //2.来到新坐标
                float endX = ev.getX();
                float endY = ev.getY();
                //3.计算偏移量
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                //4.判断滑动方向
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    //水平方向滑动

                    if (getCurrentItem() == 0 && distanceX > 0) {
                        // 2.1，当滑动到ViewPager的第0个页面，并且是从左到右滑动
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else if (getCurrentItem() == (getAdapter().getCount() - 1) && distanceX < 0) {
                        //2.2，当滑动到ViewPager的最后一个页面，并且是从右到左滑动
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        //2.3，其他
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                } else {
                    //垂直方向滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
