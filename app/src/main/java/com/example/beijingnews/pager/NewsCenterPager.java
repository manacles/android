package com.example.beijingnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

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
        String savaJson = CacheUtils.getString(context,Constants.NESCENTER_PAGER_URL);
        if (!TextUtils.isEmpty(savaJson)){
            processData(savaJson);
        }

        //联网请求数据
        getDataFromNet();
    }

    /**
     * 使用xUtils3联网请求数据
     */
    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NESCENTER_PAGER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("使用xUtils3联网请求成功==" + result);
                //缓存数据
                CacheUtils.putString(context, Constants.NESCENTER_PAGER_URL, result);

                processData(result);
                //设置适配器

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
        detailBasePagers.add(new NewsMenuDetailPager(context,data.get(0)));
        detailBasePagers.add(new TopicMenuDetailPager(context,data.get(0)));
        detailBasePagers.add(new PhotosMenuDetailPager(context));
        detailBasePagers.add(new InteractMenuDetailPager(context));


        MainActivity mainActivity = (MainActivity) context;
        LeftmenuFragment leftmenuFragment = mainActivity.getLeftmenuFragment();
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

    }
}