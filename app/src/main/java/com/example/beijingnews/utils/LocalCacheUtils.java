package com.example.beijingnews.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 本地缓存工具类
 */
public class LocalCacheUtils {

    private final MemoryCacheUtils memoryCacheUtils;

    public LocalCacheUtils(MemoryCacheUtils memoryCacheUtils) {
        this.memoryCacheUtils = memoryCacheUtils;
    }

    public Bitmap getBitmapFromLocal(String imgUrl) {
        //判断sdcard是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = MD5Encoder.encode(imgUrl);

                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews", fileName);
                if (file.exists()) {
                    LogUtil.e("图片本地地址" + file);
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                    if (bitmap != null) {
                        //在内存中缓存一份
                        memoryCacheUtils.putBitmap(imgUrl, bitmap);
                        LogUtil.e("从本地保存到内存中");
                    }

                    return bitmap;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片本地获取失败");
            }
        }

        return null;
    }

    public void putBitmap(String imgUrl, Bitmap bitmap) {
        //判断sdcard是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = MD5Encoder.encode(imgUrl);

                File fileDir = new File(Environment.getExternalStorageDirectory() + "/beijingnews");
                if (fileDir != null) {
                    fileDir.mkdirs();
                }

                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews", fileName);
                /*
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                */

                if (!file.exists()) {
                    file.createNewFile();
                }
                //保存图片
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片本地缓存失败");
            }
        }


    }
}
