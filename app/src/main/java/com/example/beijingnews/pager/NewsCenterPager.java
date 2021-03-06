package com.example.beijingnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.beijingnews.activity.MainActivity;
import com.example.beijingnews.base.BasePager;
import com.example.beijingnews.base.MenuDetailBasePager;
import com.example.beijingnews.domain.NewsCenterPagerBean;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.fragment.LeftmenuFragment;
import com.example.beijingnews.menudetailpager.InteractMenuDetailPager;
import com.example.beijingnews.menudetailpager.NewsMenuDetailPager;
import com.example.beijingnews.menudetailpager.PhotosMenuDetailPager;
import com.example.beijingnews.menudetailpager.TopicMenuDetailPager;
import com.example.beijingnews.utils.CacheUtils;
import com.example.beijingnews.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新闻中心
 */
public class NewsCenterPager extends BasePager {

    //左侧菜单对应的数据集合
    private List<NewsCenterPagerBean2.NewsData> data;

    //详情页面的集合
    private ArrayList<MenuDetailBasePager> detailBasePagers;
    private long startTime;

    public NewsCenterPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("新闻中心数据被初始化了。。。");
        ibMenu.setVisibility(View.VISIBLE);

        //1.设置标题
        tvTitle.setText("新闻中心");
        //2.联网请求，得到数据，创建视图
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        //3.把子视图添加到BasePager的FrameLayout中
        flContent.addView(textView);
        //4.绑定数据
        textView.setText("我是新闻中心的内容");

        //获取缓存数据
        String savaJson = CacheUtils.getString(context, Constants.NESCENTER_PAGER_URL);
        if (!TextUtils.isEmpty(savaJson)) {
            processData(savaJson);
        }

        startTime = SystemClock.uptimeMillis();
        //联网请求数据
//        getDataFromNet();
        getDataFromNetByVolley();
    }

    /**
     * 使用Volley联网请求数据
     */
    private void getDataFromNetByVolley() {
        //请求队列
        RequestQueue queue = Volley.newRequestQueue(context);
        //String请求
        StringRequest request = new StringRequest(Request.Method.GET,
                Constants.NESCENTER_PAGER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                long endTime = SystemClock.uptimeMillis();
                long passTime = endTime - startTime;
                LogUtil.e("使用Volley请求花的时间==" + passTime);

                LogUtil.e("使用Volley联网请求数据成功==" + response);
                //缓存数据
                CacheUtils.putString(context, Constants.NESCENTER_PAGER_URL, response);

                processData(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("使用Volley联网请求数据失败==" + error.getMessage());
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, "UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };

        //添加到队列中
        queue.add(request);
    }

    /**
     * 使用xUtils3联网请求数据
     */
    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NESCENTER_PAGER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                long endTime = SystemClock.uptimeMillis();
                long passTime = endTime - startTime;
                LogUtil.e("使用xUtils3请求花的时间==" + passTime);

                LogUtil.e("使用xUtils3联网请求成功==" + result);
                //缓存数据
                CacheUtils.putString(context, Constants.NESCENTER_PAGER_URL, result);

                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("使用xUtils3联网请求失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("使用xUtils3-onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("使用xUtils3-onFinished");
            }
        });
    }

    /**
     * 解析json数据和显示数据
     *
     * @param json
     */
    private void processData(String json) {
        //NewsCenterPagerBean bean = parsedJson(json);
        NewsCenterPagerBean2 bean2 = parsedJson2(json);

        //String title = bean.getData().get(0).getChildren().get(0).getTitle();
        String title2 = bean2.getData().get(0).getChildren().get(0).getTitle();
        //LogUtil.e(title);
        LogUtil.e(title2);


        //把数据传递给左侧菜单
        data = bean2.getData();

        //添加详情页面
        detailBasePagers = new ArrayList<>();
        detailBasePagers.add(new NewsMenuDetailPager(context, data.get(0)));
        detailBasePagers.add(new TopicMenuDetailPager(context, data.get(0)));
        detailBasePagers.add(new PhotosMenuDetailPager(context, data.get(2)));
        detailBasePagers.add(new InteractMenuDetailPager(context, data.get(2)));


        MainActivity mainActivity = (MainActivity) context;
        LeftmenuFragment leftmenuFragment = mainActivity.getLeftmenuFragment();
        data.remove(data.size() - 1);       //去掉投票
        leftmenuFragment.setData(data);
    }

    /**
     * 解析json数据：
     * 1.使用系统的Api解析json；2.使用第三方框架解析json数据，如：Gson，fastjson
     *
     * @param json
     * @return
     */
    private NewsCenterPagerBean parsedJson(String json) {
        /*
        Gson gson = new Gson();
        NewsCenterPagerBean bean = gson.fromJson(json, NewsCenterPagerBean.class);
        */
        return new Gson().fromJson(json, NewsCenterPagerBean.class);
    }

    /**
     * 手动解析
     *
     * @param json
     * @return
     */
    private NewsCenterPagerBean2 parsedJson2(String json) {
        NewsCenterPagerBean2 bean2 = new NewsCenterPagerBean2();
        try {
            JSONObject object = new JSONObject(json);

            int retcode = object.optInt("retcode");
            bean2.setRetcode(retcode);          //retcode字段解析成功

            JSONArray data = object.optJSONArray("data");
            if (data != null && data.length() > 0) {
                List<NewsCenterPagerBean2.NewsData> newsDataList = new ArrayList<>();
                //设置列表数据
                bean2.setData(newsDataList);

                //for循环，解析每条数据
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = (JSONObject) data.get(i);

                    NewsCenterPagerBean2.NewsData newsData = new NewsCenterPagerBean2.NewsData();
                    //添加到集合中
                    newsDataList.add(newsData);

                    newsData.setId(jsonObject.optInt("id"));
                    newsData.setType(jsonObject.optInt("type"));
                    newsData.setTitle(jsonObject.optString("title"));
                    newsData.setUrl(jsonObject.optString("url"));
                    newsData.setUrl1(jsonObject.optString("url1"));
                    newsData.setDayurl(jsonObject.optString("dayurl"));
                    newsData.setExcurl(jsonObject.optString("excurl"));
                    newsData.setWeekurl(jsonObject.optString("weekurl"));

                    JSONArray childrenDataArray = jsonObject.optJSONArray("children");
                    if (childrenDataArray != null && childrenDataArray.length() > 0) {
                        List<NewsCenterPagerBean2.NewsData.ChildrenData> childrenDataList = new ArrayList<>();
                        //添加到集合中
                        newsData.setChildren(childrenDataList);

                        for (int j = 0; j < childrenDataArray.length(); j++) {
                            JSONObject childrenItem = (JSONObject) childrenDataArray.get(j);

                            NewsCenterPagerBean2.NewsData.ChildrenData childrenData = new NewsCenterPagerBean2.NewsData.ChildrenData();
                            //添加到子集合中
                            childrenDataList.add(childrenData);

                            childrenData.setId(childrenItem.optInt("id"));
                            childrenData.setType(childrenItem.optInt("type"));
                            childrenData.setTitle(childrenItem.optString("title"));
                            childrenData.setUrl(childrenItem.optString("url"));
                        }
                    }

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean2;
    }

    /**
     * 根据位置切换详情页面
     *
     * @param position
     */
    public void switchPager(int position) {
        //1.设置标题
        tvTitle.setText(data.get(position).getTitle());

        //2.移除之前内容
        flContent.removeAllViews();

        //3.添加新内容
        MenuDetailBasePager detailBasePager = detailBasePagers.get(position);
        View rootView = detailBasePager.rootView;
        detailBasePager.initData();     //初始化数据
        flContent.addView(rootView);

        if (position == 2) {
            //图组详情页面
            ibSwitchListGrid.setVisibility(View.VISIBLE);
            //设置点击事件
            ibSwitchListGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.得到图组详情页面对象
                    PhotosMenuDetailPager photosMenuDetailPager = (PhotosMenuDetailPager) detailBasePagers.get(2);
                    //2.调用图组对象的切换ListView和GridView的方法
                    photosMenuDetailPager.switchListAndGrid(ibSwitchListGrid);
                }
            });
        } else if (position == 3) {
            //互动详情页面
            ibSwitchListGrid.setVisibility(View.VISIBLE);
            //设置点击事件
            ibSwitchListGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.得到图组详情页面对象
                    InteractMenuDetailPager interactMenuDetailPager = (InteractMenuDetailPager) detailBasePagers.get(3);
                    //2.调用图组对象的切换ListView和GridView的方法
                    interactMenuDetailPager.switchListAndGrid(ibSwitchListGrid);
                }
            });
        } else {
            //其他页面
            ibSwitchListGrid.setVisibility(View.GONE);
        }

    }
}