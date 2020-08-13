package com.example.beijingnews.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * 基本的Fragment，LeftFragment和ContentFragment将继承它
 */
public abstract class BaseFragment extends Fragment {

    public Context context;     //MainActivity

    //Called to do initial creation of a fragment.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    //Called to have the fragment instantiate its user interface view.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    /**
     * 子类实现自己的视图，达到自己特有的效果
     *
     * @return
     */
    public abstract View initView();

    // Called when the fragment's activity has been created and
    // this fragment's view hierarchy instantiated.
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 1.如果子页面没有数据，联网请求数据，并且绑定到initView初始化的视图上
     * 2.绑定到initView初始化的视图上
     */
    public void initData() {

    }
}