package com.example.beijingnews;

import android.app.Application;

import org.xutils.x;

/**
 * 代表整个软件
 */
public class BeijingNewApplication extends Application {
    /**
     * 所有组件被创建之前执行
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //xUtils3初始化
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
