package com.example.beijingnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.beijingnews.base.MenuDetailBasePager;
import com.example.beijingnews.domain.NewsCenterPagerBean2;

/**
 * 页签详情页面
 */
public class TabDetailPager extends MenuDetailBasePager {

    private final NewsCenterPagerBean2.NewsData.ChildrenData childrenData;
    private TextView textView;

    public TabDetailPager(Context context, NewsCenterPagerBean2.NewsData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
    }

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        textView.setText(childrenData.getTitle());
    }
}
