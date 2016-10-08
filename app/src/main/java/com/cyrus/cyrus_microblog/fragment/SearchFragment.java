package com.cyrus.cyrus_microblog.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cyrus.cyrus_microblog.BaseFragment;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.adapter.StatusAdapter;
import com.cyrus.cyrus_microblog.api.StatusesApi;
import com.cyrus.cyrus_microblog.utils.TitleBarBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * 展示公共微博，依附于MainActivity
 * <p>
 * Created by Cyrus on 2016/9/2.
 */
public class SearchFragment extends BaseFragment {

    /**
     * WeiboSDK中StatusesAPI的封装
     */
    private StatusesApi mStatusesApi;
    /**
     * 用于显示微博，支持上拉加载、下拉刷新
     */
    private PullToRefreshListView mRefreshListView;
    /**
     * 微博列表适配器
     */
    private StatusAdapter mStatusAdapter;
    /**
     * 微博列表结构体
     */
    private StatusList mStatusList;
    /**
     * 微博结构体列表
     */
    private ArrayList<Status> mStatuses;
    /**
     * 当前显示的页数
     */
    private int mCurPage;
    /**
     * 加载的页脚
     */
    private View mFootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStatusesApi = new StatusesApi(mMainActivity);
        mStatuses = new ArrayList<>();
        mCurPage = 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Fragment根视图初始化
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //TitleBar的创建
        initTitleBar(view);

        //RefreshListView的初始化,包括微博的显示和操作的监听
        initRefreshListView(view);

        return view;
    }

    private void initRefreshListView(View view) {
        mRefreshListView = (PullToRefreshListView) view.findViewById(R.id.rlv_public);
        mFootView = View.inflate(mMainActivity, R.layout.footview_loading, null);
        mStatusAdapter = new StatusAdapter(mMainActivity, mStatuses);
        mRefreshListView.setAdapter(mStatusAdapter);
        loadStatus(1);

        mRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                loadStatus(1);
            }
        });

        mRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase
                .OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadStatus(++mCurPage);
            }
        });
    }

    private void loadStatus(final int page) {
        mStatusesApi.statusPublicTimeLine(page, new RequestListener() {
            @Override
            public void onComplete(String s) {
                ProgressBar pbLoading = (ProgressBar) mFootView.findViewById(R.id.pb_loading);
                TextView tvFoot = (TextView) mFootView.findViewById(R.id.tv_foot);
                mStatusList = StatusList.parse(s);

                if (mStatusList != null) {
                    if (mStatusList.statusList != null) {
                        if (pbLoading.getVisibility() == GONE) {
                            pbLoading.setVisibility(View.VISIBLE);
                        }
                        if ("没有更多".equals(tvFoot.getText())) {
                            tvFoot.setText("加载更多");
                        }

                        if (page == 1) {
                            mStatuses.clear();
                        }

                        mStatuses.addAll(mStatusList.statusList);
                        mStatusAdapter.setStatuses(mStatuses);
                        mStatusAdapter.notifyDataSetChanged();
                    } else {
                        pbLoading.setVisibility(GONE);
                        tvFoot.setText("没有更多");
                    }

                    if (mCurPage < mStatusList.total_number) {
                        addFootView(mRefreshListView, mFootView);
                    } else {
                        removeFootView(mRefreshListView, mFootView);
                    }

                    mRefreshListView.onRefreshComplete();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
            }
        });
    }

    private void removeFootView(PullToRefreshListView refreshListView, View footView) {
        ListView lv = refreshListView.getRefreshableView();
        if (lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }

    private void addFootView(PullToRefreshListView refreshListView, View footView) {
        ListView listView = refreshListView.getRefreshableView();
        if (listView.getFooterViewsCount() == 1) {
            listView.addFooterView(footView);
        }
    }

    private void initTitleBar(View view) {
        new TitleBarBuilder(view)
                .setTitleText("发现");
    }

}
