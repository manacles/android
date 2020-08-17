package com.atguigu.androidandh5;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 作者：尚硅谷-杨光福 on 2016/7/28 11:19
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：java和js互调
 */
public class JsCallJavaCallPhoneActivity extends Activity {

    private WebView webView;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_call_java_video);

        webView = findViewById(R.id.webview);

        webSettings = webView.getSettings();
        //设置支持JavaScript
        webSettings.setJavaScriptEnabled(true);
        //设置双击变大变小
        webSettings.setUseWideViewPort(true);
        //增加缩放按钮
        webSettings.setBuiltInZoomControls(true);
        //不让从当前网页跳转到系统的浏览器中
        webView.setWebViewClient(new WebViewClient() {
            //当加载页面完成的时候回调
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });


        //添加JavaScript接口
        webView.addJavascriptInterface(new MyJavascriptInterface(), "android");

        //可以加载网络的页面，也可以加载应用内置的页面
        webView.loadUrl("file:///android_asset/JsCallJavaCallPhone.html");

    }

    private class MyJavascriptInterface {
        @JavascriptInterface
        public void showcontacts() {
//            String json = "[{\"name\":\"阿福\",\"phone\":\"4555235\"}]";
//            //调用js中的方法
//            webView.loadUrl("javascript:show(" + "'" + json + "'" + ")");

            String json = "[{\"name\":\"阿福\", \"phone\":\"18600012345\"}]";
            // 调用JS中的方法
            webView.loadUrl("javascript:show('" + json + "')");

        }

        @JavascriptInterface
        public void call(String phone) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel" + phone));

            startActivity(intent);
        }
    }
}
