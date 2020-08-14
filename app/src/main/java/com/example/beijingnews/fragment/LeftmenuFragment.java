package com.example.beijingnews.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.beijingnews.R;
import com.example.beijingnews.activity.MainActivity;
import com.example.beijingnews.base.BaseFragment;
import com.example.beijingnews.domain.NewsCenterPagerBean2;
import com.example.beijingnews.pager.NewsCenterPager;

import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.LogUtil;

import java.util.List;


/**
 * 左侧菜单的Fragment
 */
public class LeftmenuFragment extends BaseFragment {
    private static final String TAG = "LeftmenuFragment";

    private List<NewsCenterPagerBean2.NewsData> data;

    private LeftmenuFragmentAdapter adapter;

    private ListView listView;

    private int prePosition;   //点击的位置

    @Override
    public View initView() {
        Log.i(TAG, "initView: 左侧菜单视图被初始化了");

        listView = new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(40), 0, 0);
        listView.setDividerHeight(0);                       //设置分割线高度为0
        listView.setCacheColorHint(Color.TRANSPARENT);      //去除listview的拖动背景色
        listView.setSelector(android.R.color.transparent);  //设置ListView按下去的item不变色

        //设置item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.记录点击的位置，变成红色
                prePosition = position;
                adapter.notifyDataSetChanged();     //getCount()-->getView()

                //2.把左侧菜单关闭
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();     //关<---->开

                //3.切换到对应的详情页面
                switchPager(prePosition);
            }
        });
        return listView;
    }

    /**
     * 根据位置切换不同详情页面
     * @param position
     */
    private void switchPager(int position) {
        MainActivity mainActivity = (MainActivity) context;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
        newsCenterPager.switchPager(position);
    }

    @Override
    public void initData() {
        super.initData();
        Log.i(TAG, "initData: 左侧菜单数据被初始化了");
    }

    /**
     * 接受数据
     *
     * @param data
     */
    public void setData(List<NewsCenterPagerBean2.NewsData> data) {
        this.data = data;
        for (int i = 0; i < data.size(); i++) {
            LogUtil.e(data.get(i).getTitle());
        }

        //设置适配器
        adapter = new LeftmenuFragmentAdapter();
        listView.setAdapter(adapter);

        //设置默认页面
        switchPager(prePosition);
    }

    class LeftmenuFragmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
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
            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu, null);
            textView.setText(data.get(position).getTitle());
            //设置红色
            textView.setEnabled(position == prePosition);
            return textView;
        }
    }
}
