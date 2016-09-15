package com.cyrus.cyrus_microblog.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cyrus.cyrus_microblog.BaseActivity;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.adapter.StatusCommentAdapter;
import com.cyrus.cyrus_microblog.adapter.StatusGridImgsAdapter;
import com.cyrus.cyrus_microblog.api.CommentsApi;
import com.cyrus.cyrus_microblog.utils.DateUtils;
import com.cyrus.cyrus_microblog.utils.ImageOptHelper;
import com.cyrus.cyrus_microblog.utils.StringUtils;
import com.cyrus.cyrus_microblog.utils.TitleBarBuilder;
import com.cyrus.cyrus_microblog.widget.WrapHeightGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatusDetailActivity extends BaseActivity implements
        View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    /**
     * 跳转到写评论页面的请求码
     */
    private static final int REQUEST_CODE_WRITE_COMMENT = 2333;
    /**
     * 当前上下文
     */
    private Context mContext;
    /**
     * 用于加载图片
     */
    private ImageLoader mImageLoader;
    /**
     * 详情页的微博信息
     */
    private Status mStatus;
    /**
     * 是否需要滚动至评论部分
     */
    private boolean mScroll2Comment;
    /**
     * 评论当前已加载至的页数
     */
    private int mCurPage;
    /**
     * 评论的列表
     */
    private List<Comment> mComments;
    /**
     * 评论显示适配器
     */
    private StatusCommentAdapter mCommentAdapter;
    /**
     * 封装评论的Api
     */
    private CommentsApi mCommentsApi;

    // listView headerView - 微博信息
    private View mStatusDetailInfoView;
    private ImageView mIvAvatar;
    private TextView mTvSubhead;
    private TextView mTvCaption;
    private FrameLayout mIncludeStatusImage;
    private WrapHeightGridView mGvImages;
    private ImageView mIvImage;
    private TextView mTvContent;
    private View mIncludeRetweetedStatus;
    private TextView mTvRetweetedContent;
    private FrameLayout mFlRetweetedImageview;
    private WrapHeightGridView mGvRetweetedImages;
    private ImageView mIvRetweetedImage;
    // shadow_tab - 顶部悬浮的菜单栏
    private View mShadowStatusDetailTab;
    private RadioGroup mShadowRgStatusDetail;
    private RadioButton mShadowRbRetweets;
    private RadioButton mShadowRbComments;
    private RadioButton mShadowRbLikes;
    // listView headerView - 添加至列表中作为header的菜单栏
    private View mStatusDetailTabView;
    private RadioGroup mRgStatusDetail;
    private RadioButton mRbRetweets;
    private RadioButton mRbComments;
    private RadioButton mRbLikes;
    // listView - 下拉刷新控件
    private PullToRefreshListView mPlvStatusDetail;
    // mFootView - 加载更多
    private View mFootView;
    // bottom_control - 底部互动栏,包括转发/评论/点赞
    private LinearLayout mLlBottomControl;
    private LinearLayout mLlShareBottom;
    private LinearLayout mLlCommentBottom;
    private LinearLayout mLlLikeBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);

        mContext = this;
        mImageLoader = ImageLoader.getInstance();
        mCommentsApi = new CommentsApi(this);
        mComments = new ArrayList<>();
        mCurPage = 1;

        // 获取intent传入的信息
        mStatus = (Status) getIntent().getSerializableExtra("mStatus");
        mScroll2Comment = getIntent().getBooleanExtra("mScroll2Comment", false);

        // 初始化View
        initView();

        // 设置数据信息
        setData();

        // 开始加载第一页评论数据
        addFootView(mPlvStatusDetail, mFootView);
        loadComments(1);
    }

    private void initView() {
        // title - 标题栏
        initTitle();
        // listView headerView - 微博信息
        initDetailHead();
        // tab - 菜单栏
        initTab();
        // listView - 下拉刷新控件
        initListView();
        // bottom_control - 底部互动栏,包括转发/评论/点赞
        initControlBar();
    }

    private void initTitle() {
        new TitleBarBuilder(this)
                .setTitleText("微博正文")
                .setLeftImage(R.drawable.navigationbar_back_sel)
                .setLeftOnClickListener(this);
    }

    private void initDetailHead() {
        mStatusDetailInfoView = View.inflate(this, R.layout.item_status, null);
        mStatusDetailInfoView.setBackgroundResource(R.color.white);
        mStatusDetailInfoView.findViewById(R.id.ll_bottom_control).setVisibility(View.GONE);
        mIvAvatar = (ImageView) mStatusDetailInfoView.findViewById(R.id.iv_avatar);
        mTvSubhead = (TextView) mStatusDetailInfoView.findViewById(R.id.tv_subhead);
        mTvCaption = (TextView) mStatusDetailInfoView.findViewById(R.id.tv_caption);
        mIncludeStatusImage = (FrameLayout) mStatusDetailInfoView
                .findViewById(R.id.include_status_image);
        mGvImages = (WrapHeightGridView) mStatusDetailInfoView.findViewById(R.id.gv_images);
        mIvImage = (ImageView) mStatusDetailInfoView.findViewById(R.id.iv_image);
        mTvContent = (TextView) mStatusDetailInfoView.findViewById(R.id.tv_content);
        mIncludeRetweetedStatus = mStatusDetailInfoView
                .findViewById(R.id.include_retweeted_status);
        mIncludeRetweetedStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2StatusDetailActivity(mStatus.retweeted_status);
            }
        });
        mTvRetweetedContent = (TextView) mStatusDetailInfoView
                .findViewById(R.id.tv_retweeted_content);
        mFlRetweetedImageview = (FrameLayout) mIncludeRetweetedStatus
                .findViewById(R.id.include_status_image);
        mGvRetweetedImages = (WrapHeightGridView) mFlRetweetedImageview
                .findViewById(R.id.gv_images);
        mIvRetweetedImage = (ImageView) mFlRetweetedImageview.findViewById(R.id.iv_image);
        mIvImage.setOnClickListener(this);
        mGvRetweetedImages.setOnTouchInvalidPositionListener(
                new WrapHeightGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        if (motionEvent == MotionEvent.ACTION_UP) {
                            intent2StatusDetailActivity(mStatus.retweeted_status);
                        }
                        return false;
                    }
                }
        );
    }

    private void initTab() {
        // shadow
        mShadowStatusDetailTab = findViewById(R.id.status_detail_tab);
        mShadowRgStatusDetail = (RadioGroup) mShadowStatusDetailTab
                .findViewById(R.id.rg_status_detail);
        mShadowRbRetweets = (RadioButton) mShadowStatusDetailTab
                .findViewById(R.id.rb_retweets);
        mShadowRbComments = (RadioButton) mShadowStatusDetailTab
                .findViewById(R.id.rb_comments);
        mShadowRbLikes = (RadioButton) mShadowStatusDetailTab
                .findViewById(R.id.rb_likes);
        mShadowRgStatusDetail.setOnCheckedChangeListener(this);
        // header
        mStatusDetailTabView = View.inflate(this, R.layout.status_detail_tab, null);
        mRgStatusDetail = (RadioGroup) mStatusDetailTabView
                .findViewById(R.id.rg_status_detail);
        mRbRetweets = (RadioButton) mStatusDetailTabView
                .findViewById(R.id.rb_retweets);
        mRbComments = (RadioButton) mStatusDetailTabView
                .findViewById(R.id.rb_comments);
        mRbLikes = (RadioButton) mStatusDetailTabView
                .findViewById(R.id.rb_likes);
        mRgStatusDetail.setOnCheckedChangeListener(this);
    }

    private void initListView() {
        // listView - 下拉刷新控件
        mPlvStatusDetail = (PullToRefreshListView) findViewById(R.id.plv_status_detail);
        mCommentAdapter = new StatusCommentAdapter(this, mComments);
        mPlvStatusDetail.setAdapter(mCommentAdapter);
        // mFootView - 加载更多
        mFootView = View.inflate(this, R.layout.footview_loading, null);
        // Refresh View - ListView
        final ListView lv = mPlvStatusDetail.getRefreshableView();
        lv.addHeaderView(mStatusDetailInfoView);
        lv.addHeaderView(mStatusDetailTabView);
        // 下拉刷新监听
        mPlvStatusDetail.setOnRefreshListener(new PullToRefreshBase
                .OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadComments(1);
            }
        });
        // 滑动到底部最后一个item监听
        mPlvStatusDetail.setOnLastItemVisibleListener(
                new PullToRefreshBase.OnLastItemVisibleListener() {

                    @Override
                    public void onLastItemVisible() {
                        loadComments(++mCurPage);
                    }
                });
        // 滚动状态监听
        mPlvStatusDetail.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // 0-pullHead 1-detailHead 2-tab
                // 如果滑动到tab为第一个item时,则显示顶部隐藏的shadow_tab,作为悬浮菜单栏
                mShadowStatusDetailTab.setVisibility(firstVisibleItem >= 2 ?
                        View.VISIBLE : View.GONE);
            }
        });
    }

    private void initControlBar() {
        mLlBottomControl = (LinearLayout) findViewById(R.id.status_detail_controlbar);
        mLlShareBottom = (LinearLayout) mLlBottomControl.findViewById(R.id.ll_share_bottom);
        mLlCommentBottom = (LinearLayout) mLlBottomControl.findViewById(R.id.ll_comment_bottom);
        mLlLikeBottom = (LinearLayout) mLlBottomControl.findViewById(R.id.ll_like_bottom);
//        mLlBottomControl.setBackgroundResource(R.color.white);
        mLlShareBottom.setOnClickListener(this);
        mLlCommentBottom.setOnClickListener(this);
        mLlLikeBottom.setOnClickListener(this);
    }

    private void setData() {
        // listView headerView - 微博信息
        ImageLoader imageLoader = ImageLoader.getInstance();
        User user = mStatus.user;
        imageLoader.displayImage(user.profile_image_url, mIvAvatar,
                ImageOptHelper.getAvatarOptions());
        mTvSubhead.setText(user.name);
        String sourceStr = String.format("%s 来自 %s",//日期、来源
                DateUtils.getShortTime(this, mStatus.created_at),
                Html.fromHtml(mStatus.source));
        mTvCaption.setText(sourceStr);

        setImages(mStatus, mIncludeStatusImage, mGvImages, mIvImage);

        if (TextUtils.isEmpty(mStatus.text)) {
            mTvContent.setVisibility(View.GONE);
        } else {
            mTvContent.setVisibility(View.VISIBLE);
            SpannableString weiboContent = StringUtils
                    .getSpannableString(this, mTvContent, mStatus.text);
            mTvContent.setText(weiboContent);
        }

        Status retweetedStatus = mStatus.retweeted_status;
        if (retweetedStatus != null) {
            mIncludeRetweetedStatus.setVisibility(View.VISIBLE);
            String retweetContent = String.format("@%s：%s",
                    retweetedStatus.user.name, retweetedStatus.text);
            SpannableString weiboContent = StringUtils
                    .getSpannableString(this, mTvRetweetedContent, retweetContent);
            mTvRetweetedContent.setText(weiboContent);
            setImages(retweetedStatus, mFlRetweetedImageview,
                    mGvRetweetedImages, mIvRetweetedImage);
        } else {
            mIncludeRetweetedStatus.setVisibility(View.GONE);
        }

        String reportsCountStr = String.format(Locale.getDefault(), "%s %d",
                getString(R.string.item_report), mStatus.reposts_count);
        String commentsCountStr = String.format(Locale.getDefault(), "%s %d",
                getString(R.string.item_comment), mStatus.comments_count);
        String attitudesCountStr = String.format(Locale.getDefault(), "%s %d",
                getString(R.string.item_attitude), mStatus.attitudes_count);

        //shadow_tab - 顶部悬浮的菜单栏
        mShadowRbRetweets.setText(reportsCountStr);
        mShadowRbComments.setText(commentsCountStr);
        mShadowRbLikes.setText(attitudesCountStr);

        //listView headerView - 添加至列表中作为header的菜单栏
        mRbRetweets.setText(reportsCountStr);
        mRbComments.setText(commentsCountStr);
        mRbLikes.setText(attitudesCountStr);
    }

    private void setImages(final Status status, ViewGroup vgContainer,
                           GridView gvImgs, final ImageView ivImg) {
        if (status == null) {
            return;
        }

        ArrayList<String> picUrls = status.pic_urls;
        String picUrl = status.bmiddle_pic;

        if (picUrls != null && picUrls.size() == 1) {
            vgContainer.setVisibility(View.VISIBLE);
            gvImgs.setVisibility(View.GONE);
            ivImg.setVisibility(View.VISIBLE);

            mImageLoader.displayImage(picUrl, ivImg);
        } else if (picUrls != null && picUrls.size() > 1) {
            vgContainer.setVisibility(View.VISIBLE);
            gvImgs.setVisibility(View.VISIBLE);
            ivImg.setVisibility(View.GONE);

            StatusGridImgsAdapter imagesAdapter = new StatusGridImgsAdapter(this, picUrls);
            gvImgs.setAdapter(imagesAdapter);
        } else {
            vgContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 根据微博ID返回某条微博的评论列表
     *
     * @param requestPage 页数
     */
    private void loadComments(final int requestPage) {
        mCommentsApi.commentsShow(Long.parseLong(mStatus.id), requestPage, new RequestListener() {
            @Override
            public void onComplete(String s) {
                CommentList commentList = CommentList.parse(s);
                if (commentList != null) {
                    if (commentList.commentList != null) {
                        //设置footView视图
                        if (mFootView.findViewById(R.id.pb_loading).getVisibility() == View.GONE) {
                            mFootView.findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
                        }
                        if (((TextView) mFootView.findViewById(R.id.tv_foot)).getText()
                                .equals("没有更多")) {
                            ((TextView) mFootView.findViewById(R.id.tv_foot)).setText("加载更多");
                        }

                        //如果是加载第一页(第一次进入,下拉刷新)时,先清空已有数据
                        if (requestPage == 1) {
                            mComments.clear();
                        }
                        //更新评论数信息
                        String totalNumberStr = getString(R.string.item_comment)
                                + commentList.total_number;
                        mShadowRbComments.setText(totalNumberStr);
                        mRbComments.setText(totalNumberStr);

                        // 将获取的评论信息添加到列表上
                        addData(commentList);

                        // 判断是否需要滚动至评论部分
                        if (mScroll2Comment) {
                            mPlvStatusDetail.post(new Runnable() {
                                @Override
                                public void run() {
                                    mPlvStatusDetail.getRefreshableView().setSelection(2);
                                }
                            });
                            mScroll2Comment = false;
                        }
                    } else {
                        mFootView.findViewById(R.id.pb_loading).setVisibility(View.GONE);
                        ((TextView) mFootView.findViewById(R.id.tv_foot)).setText("没有更多");
                    }

                    // 用条数判断,当前评论数是否达到总评论数,未达到则添加更多加载footView,反之移除
                    if (mComments.size() < commentList.total_number) {
                        addFootView(mPlvStatusDetail, mFootView);
                    } else {
                        removeFootView(mPlvStatusDetail, mFootView);
                    }

                    mPlvStatusDetail.onRefreshComplete();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
            }
        });
    }

    private void addData(CommentList commentList) {
        // 将获取到的数据添加至列表中
        mComments.addAll(commentList.commentList);
        mCommentAdapter.setComments(mComments);
        // 添加完后,通知ListView刷新页面数据
        mCommentAdapter.notifyDataSetChanged();
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
            case R.id.iv_image:
                break;
            case R.id.ll_share_bottom:
                break;
            case R.id.ll_comment_bottom:
                // 跳转至写评论页面
                Intent intent = new Intent(this, WriteCommentActivity.class);
                intent.putExtra("mStatus", mStatus);
                startActivityForResult(intent, REQUEST_CODE_WRITE_COMMENT);
                break;
            case R.id.ll_like_bottom:
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 更新tab菜单栏某个选项时,注意header的菜单栏和shadow菜单栏的选中状态同步
        switch (checkedId) {
            case R.id.rb_retweets:
                mRbRetweets.setChecked(true);
                mShadowRbRetweets.setChecked(true);
                break;
            case R.id.rb_comments:
                mRbComments.setChecked(true);
                mShadowRbComments.setChecked(true);
                break;
            case R.id.rb_likes:
                mRbLikes.setChecked(true);
                mShadowRbLikes.setChecked(true);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 如果Back键返回,取消发评论等情况,则直接return,不做后续处理
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_WRITE_COMMENT:
                // 如果是评论发送成功的返回结果,则重新加载最新评论,同时要求滚动至评论部分
                boolean sendCommentSuccess = data.getBooleanExtra("sendCommentSuccess", false);
                if (sendCommentSuccess) {
                    mScroll2Comment = true;
                    loadComments(1);
                }
                break;
            default:
                break;
        }
    }

    private void intent2StatusDetailActivity(Status status) {
        Intent intent = new Intent(mContext, StatusDetailActivity.class);
        intent.putExtra("mStatus", status);
        mContext.startActivity(intent);
    }

}