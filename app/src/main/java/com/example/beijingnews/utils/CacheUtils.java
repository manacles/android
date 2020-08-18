package com.example.beijingnews.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import org.xutils.common.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * 缓存软件的一些参数和数据
 * */
public class CacheUtils {
    /**
     * 得到缓存值
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("buchiyu", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * 保存软件参数
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("buchiyu", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 缓存文本数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        //判断sdcard是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = MD5Encoder.encode(key);

                File fileDir = new File(Environment.getExternalStorageDirectory() + "/beijingnews/files");
                if (fileDir != null) {
                    fileDir.mkdirs();
                }

                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/files", fileName);
                /*
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                */

                if (!file.exists()) {
                    file.createNewFile();
                }
                //保存文本数据
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(value.getBytes());
                fileOutputStream.close();
                LogUtil.e("文本数据缓存成功");
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("文本数据缓存失败");
            }
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("buchiyu", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(key, value).apply();
        }

    }

    /**
     * 得到缓存的文本数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        String result = "";
        //判断sdcard是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = MD5Encoder.encode(key);

                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/files", fileName);


                if (file.exists()) {
                    FileInputStream inputStream = new FileInputStream(file);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    stream.close();
                    LogUtil.e("文本数据获取成功" + stream.toString());
                    result = stream.toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("文本数据获取失败");
            }
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("buchiyu", Context.MODE_PRIVATE);
            result = sharedPreferences.getString(key, "");
        }
        return result;
    }
}
