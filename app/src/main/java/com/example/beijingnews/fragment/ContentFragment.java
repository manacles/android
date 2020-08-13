package com.example.beijingnews.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.beijingnews.base.BaseFragment;

public class ContentFragment extends BaseFragment {
    private static final String TAG = "ContentFragment";
    private TextView textView;

    @Override
    public View initView() {
        Log.i(TAG, "initView: 正文Fragment视图被初始化了");
        textView = new TextView(context);
        textView.setTextSize(23);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        Log.i(TAG, "initData: 正文Fragment数据被初始化了");
        textView.setText("正文Fragment页面");
    }
}
