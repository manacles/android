package com.example.beijingnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.view.View;

import com.example.beijingnews.R;
import com.example.beijingnews.base.MenuDetailBasePager;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.utils.Constants;

import org.xutils.common.util.LogUtil;

/**
 * 页签详情页面
 */
public class TabDetailPager extends MenuDetailBasePager {

    private final NewsCenterPagerBean2.NewsData.ChildrenData childrenData;
    private String url;

    public TabDetailPager(Context context, NewsCenterPagerBean2.NewsData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager,null);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + childrenData.getUrl();

        LogUtil.e(childrenData.getTitle() + "的联网地址是==" + url);
    }
}
