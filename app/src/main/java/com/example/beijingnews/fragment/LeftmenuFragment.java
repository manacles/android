package com.example.beijingnews.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.beijingnews.base.BaseFragment;


/**
 * 左侧菜单的Fragment
 */
public class LeftmenuFragment extends BaseFragment {
    private static final String TAG = "LeftmenuFragment";

    private TextView textView;

    @Override
    public View initView() {
        Log.i(TAG, "initView: 左侧菜单视图被初始化了");
        textView = new TextView(context);
        textView.setTextSize(23);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        Log.i(TAG, "initData: 左侧菜单数据被初始化了");
        textView.setText("左侧菜单页面");
    }
}
