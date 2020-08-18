package com.example.beijingnews;

import android.app.Application;

import com.example.beijingnews.volley.VolleyManager;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

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
        //初始化Volley
        VolleyManager.init(this);

        //初始化极光推送
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }
}
