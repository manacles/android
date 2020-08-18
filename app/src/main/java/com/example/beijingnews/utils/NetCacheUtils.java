package com.example.beijingnews.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.example.beijingnews.domain.ImgCacheBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网络缓存工具类
 */
public class NetCacheUtils {

    //请求图片成功
    public static final int SUCCESS = 1;
    //请求图片失败
    public static final int FAIL = 2;

    private final Handler handler;
    //本地缓存工具类
    private final LocalCacheUtils localCacheUtils;
    //内存缓存工具类
    private final MemoryCacheUtils memoryCacheUtils;
    //线程池服务类
    private ExecutorService service;

    public NetCacheUtils(Handler handler, LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        this.handler = handler;
        service = Executors.newFixedThreadPool(10);

        this.localCacheUtils = localCacheUtils;
        this.memoryCacheUtils = memoryCacheUtils;
    }

    //联网请求得到图片
    public void getBitmapFromNet(String imgUrl) {
        //new Thread(new MyRunnable(imgUrl)).start();

        service.execute(new MyRunnable(imgUrl));
    }

    class MyRunnable implements Runnable {
        private final String imgUrl;

        public MyRunnable(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        @Override
        public void run() {
            //子线程
            //请求网络图片
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(imgUrl).openConnection();
                connection.setRequestMethod("GET"); //  只能大写
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);
                connection.connect();   //可写可不写
                int code = connection.getResponseCode();
                if (code == 200) {
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    ImgCacheBean imgCacheBean = new ImgCacheBean();
                    imgCacheBean.setBitmap(bitmap);
                    imgCacheBean.setImgUrl(imgUrl);

                    //显示到控件上,发消息，把bitmap发出去，把imgurl也发出去
                    Message message = Message.obtain();
                    message.what = SUCCESS;
                    message.obj = imgCacheBean;
                    handler.sendMessage(message);

                    //在内存中缓存一份
                    memoryCacheUtils.putBitmap(imgUrl,bitmap);

                    //在本地中缓存一份
                    localCacheUtils.putBitmap(imgUrl,bitmap);

                }
            } catch (IOException e) {
                e.printStackTrace();

                ImgCacheBean imgCacheBean = new ImgCacheBean();
                imgCacheBean.setImgUrl(imgUrl);

                Message message = Message.obtain();
                message.what = FAIL;
                message.obj = imgCacheBean;
                handler.sendMessage(message);
            }
        }
    }

}
