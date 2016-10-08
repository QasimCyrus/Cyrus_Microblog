package com.cyrus.cyrus_microblog.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Pull2RefreshListView extends PullToRefreshListView {

    private OnPlvScrollListener mOnPlvScrollListener;

    public Pull2RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Pull2RefreshListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    public Pull2RefreshListView(Context context, Mode mode) {
        super(context, mode);
    }

    public Pull2RefreshListView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnPlvScrollListener != null) {
            mOnPlvScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setOnPlvScrollListener(OnPlvScrollListener onPlvScrollListener) {
        mOnPlvScrollListener = onPlvScrollListener;
    }

    public interface OnPlvScrollListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

}
