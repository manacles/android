package com.example.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义下拉刷新的ListView
 */
public class RefreshListview extends ListView {

    private LinearLayout headerView;
    private View footerView;

    private View llPullDownRefresh;
    private ImageView ivArrow;
    private ProgressBar progressbarStatus;
    private TextView tvStatus;
    private TextView tvTime;
    /**
     * 下拉刷新控件的高
     */
    private int pullDownRefreshHeight;
    private int footerViewHeight;

    public static final int PULL_DOWN_REFRESH = 0;    //下拉刷新
    public static final int RELEASE_REFRESH = 1;      //手松刷新
    public static final int REFRESHING = 2;           //正在刷新

    private int currentStatus = PULL_DOWN_REFRESH;

    private Animation upAnimation;
    private Animation downAnimation;
    private float startY = -1;

    //是否已经加载更多
    private boolean isLoadMore = false;

    //顶部轮播图部分
    private View topNewsView;

    //ListView在Y轴上的坐标
    private int listViewOnScreenY = -1;


    public RefreshListview(Context context) {
        this(context, null);
    }

    public RefreshListview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView(context);
        initAnimation();
        initFooterView(context);
    }

    private void initFooterView(Context context) {
        footerView = View.inflate(context, R.layout.refresh_footer, null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();

        footerView.setPadding(0, -footerViewHeight, 0, 0);

        //ListView添加footer
        addFooterView(footerView);

        //监听ListView的滚动
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //当禁止或者惯性滚动的时候
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE || scrollState == OnScrollListener.SCROLL_STATE_FLING) {
                    //并且是最后一条 可见
                    if (getLastVisiblePosition() >= getCount() - 1) {
                        //1.显示加载更多的布局
                        footerView.setPadding(8, 8, 8, 8);
                        //2.状态改变
                        isLoadMore = true;
                        //3.回调接口
                        if (onRefreshListener != null) {
                            onRefreshListener.onLoadMore();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void initAnimation() {
        upAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180, -360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);
    }

    private void initHeaderView(Context context) {
        headerView = (LinearLayout) View.inflate(context, R.layout.refresh_header, null);
        llPullDownRefresh = headerView.findViewById(R.id.ll_pull_down_refresh);
        ivArrow = headerView.findViewById(R.id.iv_arrow);
        progressbarStatus = headerView.findViewById(R.id.progressbar_status);
        tvStatus = headerView.findViewById(R.id.tv_status);
        tvTime = headerView.findViewById(R.id.tv_time);

        //测量
        llPullDownRefresh.measure(0, 0);
        pullDownRefreshHeight = llPullDownRefresh.getMeasuredHeight();

        //默认隐藏下拉刷新控件
        llPullDownRefresh.setPadding(0, -pullDownRefreshHeight, 0, 0);   //完全隐藏

        //添加ListView头
        addHeaderView(headerView);
    }

    /**
     * 添加顶部轮播图
     *
     * @param topNewsView
     */
    public void addTopNewsView(View topNewsView) {
        if (topNewsView != null) {
            this.topNewsView = topNewsView;

            headerView.addView(topNewsView);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //1.记录起始坐标
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {
                    startY = ev.getY();
                }

                //判断顶部轮播图是否完全显示，只有完全显示才会有下拉刷新
                boolean isDisplayTopNews = isDisplayTopNews();
                if (!isDisplayTopNews) {
                    //加载更多的情况
                    break;
                }

                //如果正在刷新，就不让再刷新了
                if (currentStatus == REFRESHING) {
                    break;
                }
                //2.来到新的坐标
                float endY = ev.getY();
                //3.计算滑动的距离
                float distanceY = endY - startY;
                if (distanceY > 0) {   //下拉
                    int paddingTop = (int) (-pullDownRefreshHeight + distanceY);

                    if (paddingTop < 0 && currentStatus != PULL_DOWN_REFRESH) {
                        //下拉刷新状态
                        currentStatus = PULL_DOWN_REFRESH;
                        //更新状态
                        refreshViewState();
                    } else if (paddingTop > 0 && currentStatus != RELEASE_REFRESH) {
                        //手松刷新状态
                        currentStatus = RELEASE_REFRESH;
                        //更新状态
                        refreshViewState();
                    }

                    llPullDownRefresh.setPadding(0, paddingTop, 0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1;
                if (currentStatus == PULL_DOWN_REFRESH) {
                    llPullDownRefresh.setPadding(0, -pullDownRefreshHeight, 0, 0); //完全隐藏
                } else if (currentStatus == RELEASE_REFRESH) {
                    //设置状态为正在刷新
                    currentStatus = REFRESHING;

                    refreshViewState();

                    llPullDownRefresh.setPadding(0, 0, 0, 0);  //完全显示

                    //回调接口
                    if (onRefreshListener != null) {
                        onRefreshListener.onPullDownRefresh();
                    }
                }

                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断是否完全显示顶部轮播图
     * 当listview在屏幕上的Y轴坐标小于或者等于顶部轮播图在Y轴的坐标的时候，顶部轮播图完全显示
     *
     * @return
     */
    private boolean isDisplayTopNews() {

        if (topNewsView != null) {
            //1.得到listview在屏幕上的坐标
            int[] location = new int[2];
            if (listViewOnScreenY == -1) {
                getLocationOnScreen(location);
                listViewOnScreenY = location[1];
            }
            //2.得到顶部轮播图在屏幕上的坐标
            topNewsView.getLocationOnScreen(location);
            int topNewsViewOnScreenY = location[1];

            return listViewOnScreenY <= topNewsViewOnScreenY;
        }
        return true;
    }

    private void refreshViewState() {
        switch (currentStatus) {
            case PULL_DOWN_REFRESH:
                ivArrow.startAnimation(downAnimation);
                tvStatus.setText("下拉刷新...");
                break;
            case RELEASE_REFRESH:
                ivArrow.startAnimation(upAnimation);
                tvStatus.setText("手松刷新...");
                break;
            case REFRESHING:
                tvStatus.setText("正在刷新...");
                progressbarStatus.setVisibility(VISIBLE);
                ivArrow.clearAnimation();
                ivArrow.setVisibility(GONE);
                break;
        }
    }

    /**
     * 当联网成功和失败的时候回调该方法
     * 用户刷新状态的还原
     *
     * @param success
     */
    public void onRefreshFinish(boolean success) {

        if (isLoadMore) {
            //加载更多
            isLoadMore = false;
            //隐藏加载更多的布局
            footerView.setPadding(0, -footerViewHeight, 0, 0);
        } else {
            //下拉刷新
            tvStatus.setText("下拉刷新...");
            currentStatus = PULL_DOWN_REFRESH;
            ivArrow.clearAnimation();
            progressbarStatus.setVisibility(GONE);
            ivArrow.setVisibility(VISIBLE);
            //隐藏下拉刷新控件
            llPullDownRefresh.setPadding(0, -pullDownRefreshHeight, 0, 0);
            if (success) {
                //设置最新更新时间
                tvTime.setText("上次更新时间：" + getSystemTime());
            }
        }
    }

    /**
     * 得到当前Android系统的时间
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM--dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }


    /**
     * 监听控件的刷新
     */
    public interface OnRefreshListener {
        /**
         * 当下拉刷新的时候回调这个方法
         */
        public void onPullDownRefresh();

        /**
         * 当加载更多的时候回调这个方法
         */
        public void onLoadMore();
    }

    private OnRefreshListener onRefreshListener;

    /**
     * 设置监听刷新,由外界设置
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }
}
