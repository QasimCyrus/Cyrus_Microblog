package com.cyrus.cyrus_microblog.api;

import android.content.Context;

import com.cyrus.cyrus_microblog.constants.AccessTokenKeeper;
import com.cyrus.cyrus_microblog.constants.WBConstants;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;

/**
 * 微博接口的封装
 * <p>
 * Created by Cyrus on 2016/9/3.
 */
public class StatusesApi extends StatusesAPI {
    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context 传入的上下文
     */
    public StatusesApi(Context context) {
        super(context, WBConstants.APP_KEY, AccessTokenKeeper.readAccessToken(context));
    }

    /**
     * 获取当前登录用户及其所关注用户的最新微博
     *
     * @param page 返回结果的页码
     * @param listener 异步请求回调接口
     */
    public void statusHomeTimeLine(int page, RequestListener listener) {
        homeTimeline(0, 0, 30, page, false, 0, false, listener);
    }

    /**
     * 返回最新的公共微博。
     *
     * @param page      返回结果的页码，默认为1
     * @param requestListener  异步请求回调接口
     */
    public void statusPublicTimeLine(int page, RequestListener requestListener) {
        publicTimeline(30, page, false, requestListener);
    }

}
