package com.cyrus.cyrus_microblog.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast显示工具类
 * <p/>
 * Created by Cyrus on 2016/9/2.
 */
public class ToastUtils {

    private static Toast sToast;

    /**
     * 显示Toast
     */
    public static void showToast(Context context, CharSequence text, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context, text, duration);
        } else {
            sToast.setText(text);
            sToast.setDuration(duration);
        }
        sToast.show();
    }
}
