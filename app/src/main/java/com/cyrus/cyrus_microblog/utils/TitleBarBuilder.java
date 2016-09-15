package com.cyrus.cyrus_microblog.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyrus.cyrus_microblog.R;

/**
 * Created by Cyrus on 2016/9/3.
 */
public class TitleBarBuilder {

    private View mViewTitle;
    private TextView mTvTitle;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    private TextView mTvLeft;
    private TextView mTvRight;

    public TitleBarBuilder(Activity context) {
        mViewTitle = context.findViewById(R.id.rl_titlebar);
        mTvTitle = (TextView) mViewTitle.findViewById(R.id.tv_titlebar);
        mIvLeft = (ImageView) mViewTitle.findViewById(R.id.iv_titlebar_left);
        mIvRight = (ImageView) mViewTitle.findViewById(R.id.iv_titlebar_right);
        mTvLeft = (TextView) mViewTitle.findViewById(R.id.tv_titlebar_left);
        mTvRight = (TextView) mViewTitle.findViewById(R.id.tv_titlebar_right);
    }

    public TitleBarBuilder(View context) {
        mViewTitle = context.findViewById(R.id.rl_titlebar);
        mTvTitle = (TextView) mViewTitle.findViewById(R.id.tv_titlebar);
        mIvLeft = (ImageView) mViewTitle.findViewById(R.id.iv_titlebar_left);
        mIvRight = (ImageView) mViewTitle.findViewById(R.id.iv_titlebar_right);
        mTvLeft = (TextView) mViewTitle.findViewById(R.id.tv_titlebar_left);
        mTvRight = (TextView) mViewTitle.findViewById(R.id.tv_titlebar_right);
    }

    // title

    public TitleBarBuilder setTitleBgRes(int resid) {
        mViewTitle.setBackgroundResource(resid);
        return this;
    }

    public TitleBarBuilder setTitleText(String text) {
        mTvTitle.setVisibility(TextUtils.isEmpty(text) ? View.GONE
                : View.VISIBLE);
        mTvTitle.setText(text);
        return this;
    }

    // left

    public TitleBarBuilder setLeftImage(int resId) {
        mIvLeft.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
        mIvLeft.setImageResource(resId);
        return this;
    }

    public TitleBarBuilder setLeftText(String text) {
        mTvLeft.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        mTvLeft.setText(text);
        return this;
    }

    public TitleBarBuilder setLeftOnClickListener(View.OnClickListener listener) {
        if (mIvLeft.getVisibility() == View.VISIBLE) {
            mIvLeft.setOnClickListener(listener);
        } else if (mTvLeft.getVisibility() == View.VISIBLE) {
            mTvLeft.setOnClickListener(listener);
        }
        return this;
    }

    // right

    public TitleBarBuilder setRightImage(int resId) {
        mIvRight.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
        mIvRight.setImageResource(resId);
        return this;
    }

    public TitleBarBuilder setRightText(String text) {
        mTvRight.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        mTvRight.setText(text);
        return this;
    }

    public TitleBarBuilder setRightOnClickListener(View.OnClickListener listener) {
        if (mIvRight.getVisibility() == View.VISIBLE) {
            mIvRight.setOnClickListener(listener);
        } else if (mTvRight.getVisibility() == View.VISIBLE) {
            mTvRight.setOnClickListener(listener);
        }
        return this;
    }

    public View build() {
        return mViewTitle;
    }

}

