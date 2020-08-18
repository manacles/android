package com.example.beijingnews.domain;

import android.graphics.Bitmap;

public class ImgCacheBean {
    private Bitmap bitmap;
    private String imgUrl;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
