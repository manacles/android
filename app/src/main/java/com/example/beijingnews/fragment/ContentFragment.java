package com.example.beijingnews.fragment;

import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import androidx.viewpager.widget.ViewPager;

import com.example.beijingnews.R;
import com.example.beijingnews.base.BaseFragment;

public class ContentFragment extends BaseFragment {
    private static final String TAG = "ContentFragment";

    private ViewPager viewpager;

    private RadioGroup rgMain;

    @Override
    public View initView() {
        Log.i(TAG, "initView: 正文Fragment视图被初始化了");

        View view = View.inflate(context, R.layout.content_fragment, null);
        viewpager = view.findViewById(R.id.viewpager);
        rgMain = view.findViewById(R.id.rg_main);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.i(TAG, "initData: 正文Fragment数据被初始化了");
        rgMain.check(R.id.rb_home);
    }
}
