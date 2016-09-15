package com.cyrus.cyrus_microblog.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class Pull2RefreshScrollView extends PullToRefreshScrollView {

    private OnScrollChangeListener onScrollChangeListener;

    public Pull2RefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Pull2RefreshScrollView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        init(context);
    }

    public Pull2RefreshScrollView(Context context, Mode mode) {
        super(context, mode);
        init(context);
    }

    public Pull2RefreshScrollView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
//		lv = new InnerListView(context);
//		int height = DisplayUtils.getScreenHeightPixels((Activity)context)
//				- DisplayUtils.dp2px(context, 56 + 48 + 48);
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
//		lv.setLayoutParams(params);
//
//		FrameLayout refreshContainer = (FrameLayout) getChildAt(1);
//		ScrollView refreshView = (ScrollView) refreshContainer.getChildAt(0);
//		LinearLayout llScrollContent = (LinearLayout) refreshView.getChildAt(0);
//		llScrollContent.addView(lv);

    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public static interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

}
