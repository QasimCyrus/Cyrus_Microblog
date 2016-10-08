package com.cyrus.cyrus_microblog.activity;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.cyrus.cyrus_microblog.BaseActivity;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.adapter.StatusAdapter;
import com.cyrus.cyrus_microblog.api.SimpleRequestListener;
import com.cyrus.cyrus_microblog.api.StatusesApi;
import com.cyrus.cyrus_microblog.api.UsersApi;
import com.cyrus.cyrus_microblog.utils.ImageOptHelper;
import com.cyrus.cyrus_microblog.utils.TitleBarBuilder;
import com.cyrus.cyrus_microblog.widget.Pull2RefreshListView;
import com.cyrus.cyrus_microblog.widget.UnderlineIndicatorView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserInfoActivity extends BaseActivity implements
        OnClickListener, OnCheckedChangeListener {

    private UsersApi mUsersApi;
    private StatusesApi mStatusesApi;
    private ImageLoader mImageLoader;
    // 标题栏
    private View mTitle;
    private ImageView mIvTitlebarLeft;
    private TextView mTvTitlebar;
    // headerView - 用户信息
    private View mUserInfoHead;
    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvFollows;
    private TextView mTvFans;
    private TextView mTvSign;
    // shadow_tab - 顶部悬浮的菜单栏
    private View mShadowUserInfoTab;
    private RadioGroup mShadowRgUserInfo;
    private UnderlineIndicatorView mShadowUlivUserInfo;
    private View mUserInfoTab;
    private RadioGroup mRgUserInfo;
    private UnderlineIndicatorView mIndicatorUserInfo;
    // headerView - 添加至列表中作为header的菜单栏
    private ImageView mIvUserInfoHead;
    private Pull2RefreshListView mPlvUserInfo;
    private View mFootView;
    // 用户相关信息
    private boolean mIsCurrentUser;
    private User mUser;
    private String mUserName;
    // 个人微博列表
    private StatusList mStatusList;
    private List<Status> mStatuses;
    private StatusAdapter mStatusAdapter;
    private int mCurPage = 1;
    // 背景图片最小高度
    private int mMinImageHeight = -1;
    // 背景图片最大高度
    private int mMaxImageHeight = -1;

    private int mCurScrollY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mImageLoader = ImageLoader.getInstance();
        mUsersApi = new UsersApi(this);
        mStatusesApi = new StatusesApi(this);
        mStatuses = new ArrayList<>();

        mUserName = getIntent().getStringExtra("mUserName");
        if (TextUtils.isEmpty(mUserName)) {
            mUser = mBaseApplication.mCurrentUser;
            mIsCurrentUser = true;
        }

        initView();
        loadData();
    }

    private void initView() {
        mTitle = new TitleBarBuilder(this)
                .setTitleBgRes(R.drawable.userinfo_navigationbar_background)
                .setLeftImage(R.drawable.navigationbar_back_sel)
                .setLeftOnClickListener(this)
                .build();
        // 获取标题栏信息,需要时进行修改
        mIvTitlebarLeft = (ImageView) mTitle.findViewById(R.id.iv_titlebar_left);
        mTvTitlebar = (TextView) mTitle.findViewById(R.id.tv_titlebar);

        initInfoHead();
        initTab();
        initListView();
    }

    // 初始化用户信息
    private void initInfoHead() {
        mIvUserInfoHead = (ImageView) findViewById(R.id.iv_user_info_head);
        mUserInfoHead = View.inflate(this, R.layout.user_info_head, null);
        mIvAvatar = (ImageView) mUserInfoHead.findViewById(R.id.iv_avatar);
        mTvName = (TextView) mUserInfoHead.findViewById(R.id.tv_name);
        mTvFollows = (TextView) mUserInfoHead.findViewById(R.id.tv_follows);
        mTvFans = (TextView) mUserInfoHead.findViewById(R.id.tv_fans);
        mTvSign = (TextView) mUserInfoHead.findViewById(R.id.tv_sign);
    }

    // 初始化菜单栏
    private void initTab() {
        // 悬浮显示的菜单栏
        mShadowUserInfoTab = findViewById(R.id.user_info_tab);
        mShadowRgUserInfo = (RadioGroup) findViewById(R.id.rg_user_info);
        mShadowUlivUserInfo = (UnderlineIndicatorView) findViewById(R.id.uliv_user_info);

        mShadowRgUserInfo.setOnCheckedChangeListener(this);
        mShadowUlivUserInfo.setCurrentItemWithoutAnim(1);

        // 添加到列表中的菜单栏
        mUserInfoTab = View.inflate(this, R.layout.user_info_tab, null);
        mRgUserInfo = (RadioGroup) mUserInfoTab.findViewById(R.id.rg_user_info);
        mIndicatorUserInfo = (UnderlineIndicatorView) mUserInfoTab.findViewById(R.id.uliv_user_info);

        mRgUserInfo.setOnCheckedChangeListener(this);
        mIndicatorUserInfo.setCurrentItemWithoutAnim(1);
    }

    private void initListView() {
        mPlvUserInfo = (Pull2RefreshListView) findViewById(R.id.plv_user_info);
        initLoadingLayout();
        mFootView = View.inflate(this, R.layout.footview_loading, null);
        final ListView lv = mPlvUserInfo.getRefreshableView();
        mStatusAdapter = new StatusAdapter(this, mStatuses);
        mPlvUserInfo.setAdapter(mStatusAdapter);
        lv.addHeaderView(mUserInfoHead);
        lv.addHeaderView(mUserInfoTab);
        mPlvUserInfo.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadStatuses(1);
            }
        });
        mPlvUserInfo.setOnLastItemVisibleListener(
                new OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {
                        loadStatuses(mCurPage + 1);
                    }
                });
        mPlvUserInfo.setOnPlvScrollListener(new Pull2RefreshListView.OnPlvScrollListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                int scrollY = mCurScrollY = t;

                if (mMinImageHeight == -1) {
                    mMinImageHeight = mIvUserInfoHead.getHeight();
                }

                if (mMaxImageHeight == -1) {
                    Rect rect = mIvUserInfoHead.getDrawable().getBounds();
                    mMaxImageHeight = rect.bottom - rect.top;
                }

                if (mMinImageHeight - scrollY < mMaxImageHeight) {
                    mIvUserInfoHead.layout(0, 0, mIvUserInfoHead.getWidth(),
                            mMinImageHeight - scrollY);
                } else {
                    mIvUserInfoHead.layout(0,
                            -scrollY - (mMaxImageHeight - mMinImageHeight),
                            mIvUserInfoHead.getWidth(),
                            -scrollY - (mMaxImageHeight - mMinImageHeight)
                                    + mIvUserInfoHead.getHeight());
                }
            }
        });

        mIvUserInfoHead.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mCurScrollY == bottom - oldBottom) {
                    mIvUserInfoHead.layout(0, 0,
                            mIvUserInfoHead.getWidth(),
                            oldBottom);
                }
            }
        });
        mPlvUserInfo.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                mIvUserInfoHead.layout(0,
                        mUserInfoHead.getTop(),
                        mIvUserInfoHead.getWidth(),
                        mUserInfoHead.getTop() + mIvUserInfoHead.getHeight());

                if (mUserInfoHead.getBottom() < mTitle.getBottom()) {
                    mShadowUserInfoTab.setVisibility(View.VISIBLE);
                    mTitle.setBackgroundResource(R.drawable.navigationbar_background);
                    mIvTitlebarLeft.setImageResource(R.drawable.navigationbar_back_sel);
                    mTvTitlebar.setVisibility(View.VISIBLE);
                } else {
                    mShadowUserInfoTab.setVisibility(View.GONE);
                    mTitle.setBackgroundResource(R.drawable.userinfo_navigationbar_background);
                    mIvTitlebarLeft.setImageResource(R.drawable.userinfo_navigationbar_back_sel);
                    mTvTitlebar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initLoadingLayout() {
        ILoadingLayout loadingLayout = mPlvUserInfo.getLoadingLayoutProxy();
        loadingLayout.setPullLabel(null);
        loadingLayout.setRefreshingLabel(null);
        loadingLayout.setReleaseLabel(null);
        loadingLayout.setLoadingDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.transparent)));
    }

    private void loadData() {
        if (mIsCurrentUser) {
            // 如果是当前授权用户,直接设置信息
            setUserInfo();
        } else {
            // 如果是查看他人,调用获取用户信息接口
            loadUserInfo();
        }

        // 加载用户所属微博列表
        loadStatuses(1);
    }

    private void setUserInfo() {
        if (mUser == null) {
            return;
        }
        mTvName.setText(mUser.name);
        mTvTitlebar.setText(mUser.name);
        mImageLoader.displayImage(mUser.cover_image_phone, mIvUserInfoHead,
                ImageOptHelper.getImgOptions());
        mImageLoader.displayImage(mUser.avatar_large, new ImageViewAware(mIvAvatar),
                ImageOptHelper.getAvatarOptions());
        mTvFollows.setText(String.format(Locale.getDefault(), "关注 %d", mUser.friends_count));
        mTvFans.setText(String.format(Locale.getDefault(), "粉丝 %d", mUser.followers_count));
        mTvSign.setText(String.format(Locale.getDefault(), "简介： %s", mUser.description));
    }

    private void loadUserInfo() {
        mUsersApi.userShow(mUserName, new SimpleRequestListener(this, null) {
            @Override
            public void onComplete(String response) {
                super.onComplete(response);

                // 获取用户信息并设置
                mUser = User.parse(response);
                setUserInfo();
            }
        });
    }

    private void loadStatuses(final int requestPage) {
        mStatusesApi.statusUserTimeline(mUserName, requestPage, new RequestListener() {
            @Override
            public void onComplete(String response) {
                mStatusList = StatusList.parse(response);
                if (mStatusList != null) {
                    if (mStatusList.statusList != null) {
                        //设置footView视图
                        if (mFootView.findViewById(R.id.pb_loading).getVisibility() == View.GONE) {
                            mFootView.findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
                        }
                        if (((TextView) mFootView.findViewById(R.id.tv_foot)).getText()
                                .equals("没有更多")) {
                            ((TextView) mFootView.findViewById(R.id.tv_foot)).setText("加载更多");
                        }

                        if (requestPage == 1) {
                            mStatuses.clear();
                        }
                        mStatuses.addAll(mStatusList.statusList);
                        mStatusAdapter.setStatuses(mStatuses);
                        mStatusAdapter.notifyDataSetChanged();

                        if (mCurPage < mStatusList.total_number) {
                            addFootView(mPlvUserInfo, mFootView);
                        } else {
                            removeFootView(mPlvUserInfo, mFootView);
                        }
                    } else {
                        mFootView.findViewById(R.id.pb_loading).setVisibility(View.GONE);
                        ((TextView) mFootView.findViewById(R.id.tv_foot)).setText("没有更多");
                    }

                    mPlvUserInfo.onRefreshComplete();

                    if (mCurPage < mStatusList.total_number) {
                        addFootView(mPlvUserInfo, mFootView);
                    } else {
                        removeFootView(mPlvUserInfo, mFootView);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
            }
        });
    }


    private void addFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }

    private void removeFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_titlebar_left:
                finish();
                break;
            default:
                break;
        }
    }

    private void syncRadioButton(RadioGroup group, int checkedId) {
        int index = group.indexOfChild(group.findViewById(checkedId));

        if (mShadowUserInfoTab.getVisibility() == View.VISIBLE) {
            mShadowUlivUserInfo.setCurrentItem(index);

            ((RadioButton) mRgUserInfo.findViewById(checkedId)).setChecked(true);
            mIndicatorUserInfo.setCurrentItemWithoutAnim(index);
        } else {
            mIndicatorUserInfo.setCurrentItem(index);

            ((RadioButton) mShadowRgUserInfo.findViewById(checkedId)).setChecked(true);
            mShadowUlivUserInfo.setCurrentItemWithoutAnim(index);
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 同步悬浮和列表中的标题栏状态
        syncRadioButton(group, checkedId);
    }

}
