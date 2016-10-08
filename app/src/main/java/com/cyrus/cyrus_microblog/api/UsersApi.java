package com.cyrus.cyrus_microblog.api;

import android.content.Context;

import com.cyrus.cyrus_microblog.constants.AccessTokenKeeper;
import com.cyrus.cyrus_microblog.constants.WBConstants;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;

/**
 * 用于查询用户信息，新版微博API只允许查询到已经授予应用权限的用户
 * <p>
 * Created by Cyrus on 2016/9/23.
 */

public class UsersApi extends UsersAPI {
    public UsersApi(Context context) {
        super(context, WBConstants.APP_KEY, AccessTokenKeeper.readAccessToken(context));
    }

    /**
     * 根据用户ID获取用户信息。
     *
     * @param uid      需要查询的用户ID
     * @param listener 异步请求回调接口
     */
    public void userShow(long uid, RequestListener listener) {
        show(uid, listener);
    }

    /**
     * 根据用户昵称获取用户信息。
     *
     * @param screen_name 需要查询的用户昵称
     * @param listener    异步请求回调接口
     */
    public void userShow(String screen_name, RequestListener listener) {
        show(screen_name, listener);
    }
}
