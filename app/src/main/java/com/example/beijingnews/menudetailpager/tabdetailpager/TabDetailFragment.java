package com.example.beijingnews.menudetailpager.tabdetailpager;

import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.beijingnews.R;
import com.example.beijingnews.activity.NewsDetailActivity;
import com.example.beijingnews.base.BaseFragment;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.domain.TabDetailPagerBean;
import com.example.beijingnews.utils.CacheUtils;
import com.example.beijingnews.utils.Constants;
import com.example.beijingnews.view.RecyclerViewOnUpScrollListener;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 页签详情页面
 */
public class TabDetailFragment extends BaseFragment {

    public static final String READ_ARRAY_ID = "read_array_id";
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
/*    @BindView(R.id.ll_footerview)
    View llFooterView;*/


    private final NewsCenterPagerBean2.NewsData.ChildrenData childrenData;
    private String url;
    //底部ListView列表的数据
    private List<TabDetailPagerBean.DataBean.NewsBean> news;

    //下一页的联网路径
    private String moreUrl;
    //是否加载更多
    private boolean isLoadMore = false;
    private TabDetailAdapter adapter;

    private TabDetailPagerBean.DataBean data;
    private Parcelable recyclerViewState;

    public TabDetailFragment(NewsCenterPagerBean2.NewsData.ChildrenData childrenData) {
        this.childrenData = childrenData;
    }


    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        ButterKnife.bind(this, view);

        swiperefreshlayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(context, "下拉刷新了", Toast.LENGTH_SHORT).show();
                getDataFromNet();
            }
        });

        recyclerview.setOnScrollListener(new RecyclerViewOnUpScrollListener() {
            @Override
            public void onLoadMore() {
                adapter.setLoading(true);
                getMoreDataFromNet();
            }
        });

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + childrenData.getUrl();
        LogUtil.e(childrenData.getTitle() + "的联网地址是==" + url);

        //把缓存数据取出来
        String saveJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        //联网请求数据
        getDataFromNet();

    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LogUtil.e("加载更多请求成功==" + result);
                isLoadMore = true;

                //解析数据
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("加载更多请求失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("加载更多请求onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("加载更多请求onFinished");
            }
        });
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CacheUtils.putString(context, url, result);
                LogUtil.e(childrenData.getTitle() + "-页面数据请求成功==" + result);
                swiperefreshlayout.setRefreshing(false);
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求失败==" + ex.getMessage());
                swiperefreshlayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求onFinished");
            }
        });
    }

    private void processData(String json) {
        TabDetailPagerBean bean = parseJson(json);

        moreUrl = TextUtils.isEmpty(bean.getData().getMore()) ? "" : (Constants.BASE_URL + bean.getData().getMore());
        Log.e("调试中", "processData:moreUrl " + moreUrl);
        if (!isLoadMore) {
            data = bean.getData();
            news = data.getNews();

            //默认
            recyclerview.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            adapter = new TabDetailAdapter(context, data);
            recyclerview.setAdapter(adapter);
        } else {
            //加载更多
            isLoadMore = false;


            //添加到原来的集合中
            news.addAll(bean.getData().getNews());

            //刷新适配器
            adapter.setLoading(false);
            //adapter.notifyDataSetChanged();
        }

        if (adapter.getItemCount() > 1) {
            adapter.setOnItemClickListener(new TabDetailAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    TabDetailPagerBean.DataBean.NewsBean newsBean = news.get(position - 1);
//                Toast.makeText(context, "newsBean:id==" + newsBean.getId() + "title:" + newsBean.getTitle(), Toast.LENGTH_SHORT).show();
                    LogUtil.e("newsBean:id==" + newsBean.getId() + "title:" + newsBean.getTitle() + ",url=" + newsBean.getUrl());

                    //1.取出保存的Id集合
                    String idArray = CacheUtils.getString(context, READ_ARRAY_ID);
                    //2.判断是否存在，如果不存在，才保存，并且刷新适配器
                    if (!idArray.contains(newsBean.getId() + "")) {
                        CacheUtils.putString(context, READ_ARRAY_ID, idArray + newsBean.getId() + ",");

                        //刷新适配器
                        adapter.notifyDataSetChanged();
                    }

                    //跳转到新闻浏览页面
                    Intent intent = new Intent(context, NewsDetailActivity.class);
                    intent.putExtra("url", Constants.BASE_URL + newsBean.getUrl());
                    context.startActivity(intent);
                }
            });
        }

    }


    private TabDetailPagerBean parseJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }
}
