package com.cyrus.cyrus_microblog.api;

import android.content.Context;

import com.cyrus.cyrus_microblog.constants.AccessTokenKeeper;
import com.cyrus.cyrus_microblog.constants.WBConstants;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;

/**
 * 评论接口的封装
 * <p/>
 * Created by Cyrus on 2016/9/10.
 */
public class CommentsApi extends CommentsAPI {
    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context
     */
    public CommentsApi(Context context) {
        super(context, WBConstants.APP_KEY, AccessTokenKeeper.readAccessToken(context));
    }

    /**
     * 根据微博ID返回某条微博的评论列表。
     *
     * @param id       需要查询的微博ID。
     * @param page     返回结果的页码.
     * @param listener 异步请求回调接口
     */
    public void commentsShow(long id, int page, RequestListener listener) {
        show(id, 0, 0, 50, page, 0, listener);
    }

    /**
     * 对一条微博进行评论。
     *
     * @param comment     评论内容，内容不超过140个汉字。
     * @param id          需要评论的微博ID。
     * @param listener    异步请求回调接口
     */
    public void commentCreate(String comment, long id, RequestListener listener) {
        create(comment, id, true, listener);
    }

    /**
     * 回复一条评论。
     *
     * @param cid             需要回复的评论ID
     * @param id              需要评论的微博ID
     * @param comment         回复评论内容，内容不超过140个汉字
     * @param without_mention 回复中是否自动加入“回复@用户名”，true：是、false：否，默认为false
     * @param comment_ori     当评论转发微博时，是否评论给原微博，false：否、true：是，默认为false
     * @param listener        异步请求回调接口
     */
    public void reply(long cid, long id, String comment, boolean without_mention, boolean comment_ori,
                      RequestListener listener) {

    }
}
