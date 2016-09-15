package com.cyrus.cyrus_microblog.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Pull2RefreshListView extends PullToRefreshListView {

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
    protected ListView createListView(Context context, AttributeSet attrs) {
        System.out.println("createListView createListView createListView");
        ListView listView = super.createListView(context, attrs);
//		listView.setMinimumHeight(DisplayUtils.getScreenHeightPixels((Activity)context));
        return listView;
    }

}
