package com.example.beijingnews.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 内存缓存工具类
 */
class MemoryCacheUtils {

    //集合
    private LruCache<String, Bitmap> lruCache;

    public MemoryCacheUtils() {
        //使用系统分配给应用程序的八分之一内存来作为缓存大小
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //return super.sizeOf(key, value);
                return value.getByteCount();
            }
        };
    }

    //根据url在内存中获取图片
    public Bitmap getBitmapFromMemory(String imgUrl) {
        return lruCache.get(imgUrl);
    }

    //根据url保存图片到lruCache集合中
    public void putBitmap(String imgUrl, Bitmap bitmap) {
        lruCache.put(imgUrl, bitmap);
    }
}
