package com.example.beijingnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beijingnews.activity.GuideActivity;
import com.example.beijingnews.activity.MainActivity;
import com.example.beijingnews.utils.CacheUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    //静态常量，用于记录是否跳过引导页
    public static final String START_MAIN = "start_main";

    @BindView(R.id.ll_splash_root)
    LinearLayout llSplashRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        //透明度 渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 0.7f);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        AnimationSet animationSet = new AnimationSet(false);
        //添加三个动画没有先后顺序,便于同时播放动画
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setFillAfter(true);            //动画终止时停留在最后一帧
        animationSet.setDuration(2000);             //持续播放时间

        llSplashRoot.startAnimation(animationSet);

        animationSet.setAnimationListener(new MyAnimationListener());

    }

    class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //判断是否进入过引导页面
            boolean isStartMain = CacheUtils.getBoolean(SplashActivity.this, START_MAIN);
            Intent intent;
            if (isStartMain) {
                //进入过则直接进入主页面
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                //未进入过则进入引导页面
                intent = new Intent(SplashActivity.this, GuideActivity.class);
            }
            startActivity(intent);
            //关闭splash页面
            finish();

            //Toast.makeText(SplashActivity.this,"动画播放完成了",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
