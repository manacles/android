package com.example.beijingnews.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.beijingnews.R;
import com.example.beijingnews.activity.MainActivity;

/**
 * 基类或者说公共类
 */
public class BasePager {

    //上下文
    public final Context context;   //MainActivity
    //视图，代表各个不同的页面
    public View rootView;
    //显示标题
    public TextView tvTitle;
    //点击侧滑的
    public ImageButton ibMenu;
    //加载各个子页面的
    public FrameLayout flContent;

    //组图页面的切换按钮
    public ImageButton ibSwitchListGrid;

    public BasePager(Context context) {
        this.context = context;
        //构造方法一执行，视图就被初始化了
        rootView = initView();
    }

    /**
     * 用于初始化公共部分视图，并且初始化加载子视图的FrameLayout
     *
     * @return
     */
    private View initView() {
        //基类的页面
        View view = View.inflate(context, R.layout.base_pager, null);
        tvTitle = view.findViewById(R.id.tv_title);
        ibMenu = view.findViewById(R.id.ib_menu);
        flContent = view.findViewById(R.id.fl_content);
        ibSwitchListGrid = view.findViewById(R.id.ib_switch_list_grid);

        ibMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();     //开<--->关
            }
        });

        return view;
    }

    /**
     * 初始化数据：当子类需要初始化数据，或者绑定数据，联网请求数据并且绑定的时候，重写该方法
     */
    public void initData() {

    }
}
