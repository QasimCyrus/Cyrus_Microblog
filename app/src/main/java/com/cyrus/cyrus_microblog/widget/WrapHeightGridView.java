package com.cyrus.cyrus_microblog.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class WrapHeightGridView extends GridView {

    private OnTouchInvalidPositionListener mTouchInvalidPosListener;

    public WrapHeightGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WrapHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapHeightGridView(Context context) {
        super(context);
    }

    /**
     * 重新测定GridView的宽和高，使其符合在ListView中的高度，避免显示不全
     *
     * @param widthMeasureSpec  原先传入的宽
     * @param heightMeasureSpec 原先传入的高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mTouchInvalidPosListener == null) {
            return super.onTouchEvent(ev);
        }

        if (!isEnabled()) {
            return isClickable() || isLongClickable();
        }

        int motionPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
        if (motionPosition == INVALID_POSITION) {
            super.onTouchEvent(ev);
            return mTouchInvalidPosListener
                    .onTouchInvalidPosition(ev.getActionMasked());
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 点击空白区域时的响应和处理接口
     */
    public void setOnTouchInvalidPositionListener(
            OnTouchInvalidPositionListener listener) {
        mTouchInvalidPosListener = listener;
    }

    public interface OnTouchInvalidPositionListener {
        /**
         * motionEvent 可使用 MotionEvent.ACTION_DOWN 或者
         * MotionEvent.ACTION_UP等来按需要进行判断
         *
         * @return 是否要终止事件的路由
         */
        boolean onTouchInvalidPosition(int motionEvent);
    }
}
