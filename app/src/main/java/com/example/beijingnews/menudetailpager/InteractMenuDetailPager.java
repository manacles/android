package com.example.beijingnews.menudetailpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beijingnews.R;
import com.example.beijingnews.base.MenuDetailBasePager;
import com.example.beijingnews.domain.ImgCacheBean;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.domain.PhotosMenuDetailPagerBean;
import com.example.beijingnews.utils.BitmapCacheUtils;
import com.example.beijingnews.utils.CacheUtils;
import com.example.beijingnews.utils.Constants;
import com.example.beijingnews.utils.NetCacheUtils;
import com.example.beijingnews.volley.VolleyManager;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 互动详情页面
 */
public class InteractMenuDetailPager extends MenuDetailBasePager {

    private final NewsCenterPagerBean2.NewsData newsData;
    @ViewInject(R.id.listview)
    private ListView listView;

    @ViewInject(R.id.gridview)
    private GridView gridView;

    private String url;
    private List<PhotosMenuDetailPagerBean.DataBean.NewsBean> news;

    private PhotosMenuDetailPagerAdapter adapter;

    //图片三级缓存工具类
    private BitmapCacheUtils bitmapCacheUtils;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ImgCacheBean imgCacheBean = (ImgCacheBean) msg.obj;
            String imgUrl = imgCacheBean.getImgUrl();
            Bitmap bitmap = imgCacheBean.getBitmap();
            switch (msg.what) {
                case NetCacheUtils.SUCCESS:     //图片请求成功
                    LogUtil.e("请求图片成功===imgUrl" + imgUrl);
                    LogUtil.e("请求图片成功===bitmap" + bitmap);
                    if (isShowListView) {
                        ImageView imageView = listView.findViewWithTag(imgUrl);
                        if (imageView != null && bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    } else {
                        ImageView imageView = gridView.findViewWithTag(imgUrl);
                        if (imageView != null && bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }

                    break;
                case NetCacheUtils.FAIL:        //图片请求失败
                    LogUtil.e("请求图片失败===imgUrl" + imgUrl);
                    LogUtil.e("请求图片失败===bitmap" + bitmap);
                    break;
            }
        }
    };

    public InteractMenuDetailPager(Context context, NewsCenterPagerBean2.NewsData newsData) {
        super(context);
        this.newsData = newsData;
        bitmapCacheUtils = new BitmapCacheUtils(handler);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.photos_menu_detail_pager, null);
        x.view().inject(InteractMenuDetailPager.this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("互动详情页面的数据初始化了");

        url = Constants.BASE_URL + newsData.getUrl();

        String savaJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(savaJson)) {
            processData(savaJson);
        }

        getDataFromNet();
    }

    /**
     * true,显示ListView，隐藏GridView；
     * false显示GridView，隐藏ListView;
     */
    private boolean isShowListView = true;

    public void switchListAndGrid(ImageButton ibSwitchListGrid) {
        if (isShowListView) {
            isShowListView = false;
            //显示GridView，隐藏ListView
            gridView.setVisibility(View.VISIBLE);
            adapter = new PhotosMenuDetailPagerAdapter();
            gridView.setAdapter(adapter);
            listView.setVisibility(View.GONE);
            //按钮显示--ListView
            ibSwitchListGrid.setImageResource(R.drawable.icon_pic_list_type);
        } else {
            isShowListView = true;
            //显示ListView，隐藏GridView
            listView.setVisibility(View.VISIBLE);
            adapter = new PhotosMenuDetailPagerAdapter();
            listView.setAdapter(adapter);
            gridView.setVisibility(View.GONE);
            //按钮显示--GridView
            ibSwitchListGrid.setImageResource(R.drawable.icon_pic_grid_type);
        }
    }

    /**
     * 使用Volley联网请求数据
     */
    private void getDataFromNet() {

        //String请求
        StringRequest request = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                LogUtil.e("使用Volley联网请求数据成功==" + response);
                //缓存数据
                CacheUtils.putString(context, url, response);

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
        VolleyManager.getRequestQueue().add(request);
    }

    /**
     * 解析和显示数据
     *
     * @param json
     */
    private void processData(String json) {
        PhotosMenuDetailPagerBean bean = parsedJson(json);
        String title = bean.getData().getNews().get(0).getTitle();
        LogUtil.e("互动页面数据获取title==" + title);

        //设置适配器
        news = bean.getData().getNews();
        adapter = new PhotosMenuDetailPagerAdapter();
        listView.setAdapter(adapter);
    }

    /**
     * 使用fastjson解析json字符串
     */
    private PhotosMenuDetailPagerBean parsedJson(String json) {
        // fastjson: VO vo = JSON.parseObject("{...}", VO.class); //反序列化
        return JSON.parseObject(json, PhotosMenuDetailPagerBean.class);
    }

    class PhotosMenuDetailPagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_photos_menu_detail_pager, null);
                holder = new ViewHolder();
                holder.imageView = convertView.findViewById(R.id.iv_listimage);
                holder.textView = convertView.findViewById(R.id.tv_title);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到对应的数据
            PhotosMenuDetailPagerBean.DataBean.NewsBean newsBean = news.get(position);
            holder.textView.setText(newsBean.getTitle());

            String imgUrl = Constants.BASE_URL + newsBean.getListimage();
            //1.使用Volley请求图片-设置图片
            //loaderImager(holder, imgUrl);
            //2.使用自定义的三级缓存工具类请求图片
            /*holder.imageView.setTag(imgUrl);
            Bitmap bitmap = bitmapCacheUtils.getBitmap(imgUrl); //内存或者本地
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            }*/
            //3.使用Picasso请求图片
            /*Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.drawable.home_scroll_default)
                    .error(R.drawable.home_scroll_default)
                    .into(holder.imageView);*/
            //4.使用Glide请求图片
            Glide.with(context)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.home_scroll_default)
                    .error(R.drawable.home_scroll_default)
                    .into(holder.imageView);
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    private void loaderImager(final ViewHolder viewHolder, String imageUrl) {
        //设置tag
        viewHolder.imageView.setTag(imageUrl);
        //直接在这里请求会乱位置
        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response != null) {
                    if (viewHolder.imageView != null) {
                        if (response.getBitmap() != null) {
                            //设置图片
                            viewHolder.imageView.setImageBitmap(response.getBitmap());
                        } else {
                            //设置默认图片
                            viewHolder.imageView.setImageResource(R.drawable.home_scroll_default);
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                //如果出错，则说明都不显示（简单处理），最好准备一张出错图片
                viewHolder.imageView.setImageResource(R.drawable.home_scroll_default);
            }
        };
        VolleyManager.getImageLoader().get(imageUrl, listener);
    }

}
