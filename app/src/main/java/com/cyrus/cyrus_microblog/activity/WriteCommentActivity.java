package com.cyrus.cyrus_microblog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cyrus.cyrus_microblog.BaseActivity;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.api.CommentsApi;
import com.cyrus.cyrus_microblog.utils.TitleBarBuilder;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.Status;

public class WriteCommentActivity extends BaseActivity implements View.OnClickListener {

    // 评论输入框
    private EditText mEtWriteStatus;
    // 底部按钮
    private ImageView mIvImage;
    private ImageView mIvAt;
    private ImageView mIvTopic;
    private ImageView mIvEmoji;
    private ImageView mIvAdd;
    /**
     * 待评论的微博
     */
    private Status mStatus;
    /**
     * 封装评论的Api
     */
    private CommentsApi mCommentsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        //初始化Api
        mCommentsApi = new CommentsApi(this);

        // 获取Intent传入的微博
        mStatus = (Status) getIntent().getSerializableExtra("mStatus");

        initView();

    }

    private void initView() {
        new TitleBarBuilder(this)
                .setTitleText("发评论")
                .setLeftText("取消")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 取消发送评论,关闭本页面
                        WriteCommentActivity.this.finish();
                    }
                })
                .setRightText("发送")
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendComment();
                    }
                });

        mEtWriteStatus = (EditText) findViewById(R.id.et_write_status);
        mIvImage = (ImageView) findViewById(R.id.iv_image);
        mIvAt = (ImageView) findViewById(R.id.iv_at);
        mIvTopic = (ImageView) findViewById(R.id.iv_topic);
        mIvEmoji = (ImageView) findViewById(R.id.iv_emoji);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);

        mIvImage.setOnClickListener(this);
        mIvAt.setOnClickListener(this);
        mIvTopic.setOnClickListener(this);
        mIvEmoji.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
    }

    private void sendComment() {
        String comment = mEtWriteStatus.getText().toString();
        if (TextUtils.isEmpty(comment)) {
            showToast("评论内容不能为空");
            return;
        }

        mCommentsApi.commentCreate(comment, Long.parseLong(mStatus.id), new RequestListener() {
            @Override
            public void onComplete(String response) {
                showToast("评论发送成功");

                // 微博发送成功后,设置Result结果数据,然后关闭本页面
                Intent data = new Intent();
                data.putExtra("sendCommentSuccess", true);
                setResult(RESULT_OK, data);

                WriteCommentActivity.this.finish();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_image:
                break;
            case R.id.iv_at:
                break;
            case R.id.iv_topic:
                break;
            case R.id.iv_emoji:
                break;
            case R.id.iv_add:
                break;
        }
    }

}
