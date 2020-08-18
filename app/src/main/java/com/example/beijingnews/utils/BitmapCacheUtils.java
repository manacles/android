package com.example.beijingnews.utils;

import android.graphics.Bitmap;
import android.os.Handler;

import org.xutils.common.util.LogUtil;

/**
 * 图片缓存的工具类
 */
public class BitmapCacheUtils {


    /**
     * 三级缓存设计步骤：
     * * 从内存中取出图片
     * * 从本地文件中取图片
     * 向内存中保存一份
     * * 请求网络图片，获取图片，显示到控件上
     * 向内存存一份
     * 向本地文件存一份
     */

    //网络缓存工具类
    private NetCacheUtils netCacheUtils;
    //本地缓存工具类
    private LocalCacheUtils localCacheUtils;
    //内存缓存工具类
    private MemoryCacheUtils memoryCacheUtils;

    public BitmapCacheUtils(Handler handler) {
        memoryCacheUtils = new MemoryCacheUtils();

        localCacheUtils = new LocalCacheUtils(memoryCacheUtils);

        netCacheUtils = new NetCacheUtils(handler,localCacheUtils,memoryCacheUtils);
    }

    public Bitmap getBitmap(String imgUrl) {
        //1.内存获取
        if (memoryCacheUtils != null) {
            Bitmap bitmap = memoryCacheUtils.getBitmapFromMemory(imgUrl);
            if (bitmap != null) {
                LogUtil.e("内存加载图片成功===" + imgUrl);
                return bitmap;
            }
        }
        //2.本地获取
        if (localCacheUtils != null) {
            Bitmap bitmap = localCacheUtils.getBitmapFromLocal(imgUrl);
            if (bitmap != null) {
                LogUtil.e("本地加载图片成功===" + imgUrl);
                return bitmap;
            }
        }

        //3.网络获取
        netCacheUtils.getBitmapFromNet(imgUrl);

        return null;
    }
}
