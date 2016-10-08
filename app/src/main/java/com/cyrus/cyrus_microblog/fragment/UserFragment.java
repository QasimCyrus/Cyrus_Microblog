package com.cyrus.cyrus_microblog.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cyrus.cyrus_microblog.BaseFragment;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.activity.UserInfoActivity;
import com.cyrus.cyrus_microblog.adapter.UserItemAdapter;
import com.cyrus.cyrus_microblog.api.UsersApi;
import com.cyrus.cyrus_microblog.constants.AccessTokenKeeper;
import com.cyrus.cyrus_microblog.model.UserItem;
import com.cyrus.cyrus_microblog.utils.TitleBarBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.cyrus.cyrus_microblog.R.id.iv_avatar;
import static com.cyrus.cyrus_microblog.R.id.lv_user_items;
import static com.cyrus.cyrus_microblog.R.id.tv_caption;
import static com.cyrus.cyrus_microblog.R.id.tv_fans_count;
import static com.cyrus.cyrus_microblog.R.id.tv_follow_count;
import static com.cyrus.cyrus_microblog.R.id.tv_status_count;
import static com.cyrus.cyrus_microblog.R.id.tv_subhead;

/**
 * 展示使用者的用户信息，依附于MainActivity
 * <p>
 * Created by Cyrus on 2016/9/2.
 */
public class UserFragment extends BaseFragment {

    private LinearLayout mLlUserInfo;

    private ImageView mIvAvatar;
    private TextView mTvSubhead;
    private TextView mTvCaption;

    private TextView mTvStatusCount;
    private TextView mTvFollowCount;
    private TextView mTvFansCount;

    private ListView mLvUserItems;
    private UserItemAdapter mUserItemAdapter;
    private List<UserItem> mUserItems;

    private Oauth2AccessToken mAccessToken;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoader = ImageLoader.getInstance();
        mAccessToken = AccessTokenKeeper.readAccessToken(mMainActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        initView(view);
        setItem();

        return view;
    }

    private void setItem() {
        mUserItems.add(new UserItem(false, R.drawable.push_icon_app_small_1, "新的朋友", ""));
        mUserItems.add(new UserItem(false, R.drawable.push_icon_app_small_2, "微博等级", "Lv13"));
        mUserItems.add(new UserItem(false, R.drawable.push_icon_app_small_3, "编辑资料", ""));
        mUserItems.add(new UserItem(true, R.drawable.push_icon_app_small_4, "我的相册", "(18)"));
        mUserItems.add(new UserItem(false, R.drawable.push_icon_app_small_5, "我的点评", ""));
        mUserItems.add(new UserItem(false, R.drawable.push_icon_app_small_4, "我的赞", "(32)"));
        mUserItems.add(new UserItem(true, R.drawable.push_icon_app_small_3, "微博支付", ""));
        mUserItems.add(new UserItem(false, R.drawable.push_icon_app_small_2, "微博运动", "步数、卡路里、跑步轨迹"));
        mUserItems.add(new UserItem(true, R.drawable.push_icon_app_small_1, "更多", "收藏、名片"));
        mUserItemAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {
        //标题栏
        new TitleBarBuilder(view)
                .setTitleText("我")
                .build();
        //用户信息
        mLlUserInfo = (LinearLayout) view.findViewById(R.id.ll_user_info);
        mLlUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UsersApi(mMainActivity).userShow(Long.parseLong(mAccessToken
                        .getUid()), new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        User currentUser = User.parse(s);
                        if (currentUser != null) {
                            Intent intent = new Intent(mMainActivity, UserInfoActivity.class);
                            intent.putExtra("mUserName", currentUser.name);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        mIvAvatar = (ImageView) view.findViewById(iv_avatar);
        mTvSubhead = (TextView) view.findViewById(tv_subhead);
        mTvCaption = (TextView) view.findViewById(tv_caption);
        // 互动信息栏: 微博数、关注数、粉丝数
        mTvStatusCount = (TextView) view.findViewById(tv_status_count);
        mTvFollowCount = (TextView) view.findViewById(tv_follow_count);
        mTvFansCount = (TextView) view.findViewById(tv_fans_count);
        // 设置栏列表
        mLvUserItems = (ListView) view.findViewById(lv_user_items);
        mUserItems = new ArrayList<>();
        mUserItemAdapter = new UserItemAdapter(getContext(), mUserItems);
        mLvUserItems.setAdapter(mUserItemAdapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            new UsersApi(mMainActivity).userShow(Long.parseLong(mAccessToken
                    .getUid()), new RequestListener() {
                @Override
                public void onComplete(String s) {
                    User currentUser = User.parse(s);
                    if (currentUser != null) {
                        setUserInfo(currentUser);
                    }
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void setUserInfo(User user) {
        mTvSubhead.setText(user.name);
        mTvCaption.setText(String.format(Locale.getDefault(), "简介：%s", user.description));
        mImageLoader.displayImage(user.avatar_large, mIvAvatar);
        mTvStatusCount.setText(String.format(Locale.getDefault(), "%d", user.statuses_count));
        mTvFollowCount.setText(String.format(Locale.getDefault(), "%d", user.friends_count));
        mTvFansCount.setText(String.format(Locale.getDefault(), "%d", user.followers_count));
    }
}
