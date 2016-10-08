package com.cyrus.cyrus_microblog.api;

import android.content.Context;
import android.text.TextUtils;

import com.cyrus.cyrus_microblog.constants.AccessTokenKeeper;
import com.cyrus.cyrus_microblog.constants.WBConstants;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;

/**
 * 微博接口的封装
 * <p/>
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
     * @param page     返回结果的页码
     * @param listener 异步请求回调接口
     */
    public void statusHomeTimeLine(int page, RequestListener listener) {
        homeTimeline(0, 0, 30, page, false, FEATURE_ALL, false, listener);
    }

    /**
     * 获取当前用户最新发表的微博列表
     *
     * @param screen_name   需要查询的用户昵称
     * @param page          返回结果的页码，默认为1
     * @param listener      异步请求回调接口
     */
    public void statusUserTimeline(String screen_name, int page, RequestListener listener) {
        userTimeline(screen_name, 0, 0, 30, page, false, FEATURE_ALL, false, listener);
    }

    /**
     * 返回最新的公共微博。
     *
     * @param page            返回结果的页码，默认为1
     * @param requestListener 异步请求回调接口
     */
    public void statusPublicTimeLine(int page, RequestListener requestListener) {
        publicTimeline(30, page, false, requestListener);
    }

    /**
     * 发布或转发一条微博
     *
     * @param status           要发布的微博文本内容，内容不超过140个汉字。
     *                         如果是转发微博，不填则默认发送“转发微博”
     * @param imageUrl         图片的URL地址，必须以http开头，为空则代表发布无图微博
     * @param retweetedStatsId 要转发的微博ID(<=0时为原创微博)
     * @param listener         异步请求回调接口
     */
    public void statusSend(String status, String imageUrl,
                           long retweetedStatsId, RequestListener listener) {
        if (retweetedStatsId > 0) {
            repost(retweetedStatsId, status, COMMENTS_NONE, listener);
        } else {
            if (TextUtils.isEmpty(imageUrl)) {
                update(status, "0.0", "0.0", listener);
            } else {
                uploadUrlText(status, imageUrl, null, "0.0", "0.0", listener);
            }
        }
    }

    /**
     * 根据微博ID删除指定微博。
     *
     * @param id       需要删除的微博ID
     * @param listener 异步请求回调接口
     */
    public void statusDestory(long id, RequestListener listener) {
        destroy(id, listener);
    }
}
