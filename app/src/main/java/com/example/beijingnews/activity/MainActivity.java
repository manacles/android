package com.example.beijingnews.activity;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.beijingnews.R;
import com.example.beijingnews.fragment.ContentFragment;
import com.example.beijingnews.fragment.LeftmenuFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String MAIN_CONTENT_TAG = "main_content_tag";
    public static final String LEFTMENU_TAG = "leftmenu_tag";

    @BindView(R.id.fl_main_content)
    FrameLayout flMainContent;
    @BindView(R.id.fl_leftmenu)
    FrameLayout flLeftmenu;
    @BindView(R.id.drawerlayout)
    DrawerLayout drawerlayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //初始化Fragment
        initFragment();
    }

    private void initFragment() {
        /*
        //1.得到FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //3.替换
        transaction.replace(R.id.fl_main_content, new ContentFragment(), MAIN_CONTENT_TAG);  //主页
        transaction.replace(R.id.fl_leftmenu, new LeftmenuFragment(), LEFTMENU_TAG);  //主页
        //4.提交
        transaction.commit();
        */
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main_content, new ContentFragment(), MAIN_CONTENT_TAG)
                .replace(R.id.fl_leftmenu, new LeftmenuFragment(), LEFTMENU_TAG)
                .commit();
    }

    /**
     * 得到左侧菜单Fragment
     *
     * @return
     */
    public LeftmenuFragment getLeftmenuFragment() {
        return (LeftmenuFragment) getSupportFragmentManager().findFragmentByTag(LEFTMENU_TAG);
    }

    /**
     * 得到正文的Fragment
     *
     * @return
     */
    public ContentFragment getContentFragment() {
        return (ContentFragment) getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT_TAG);
    }

    public DrawerLayout getDrawerlayout() {
        return drawerlayout;
    }
}
