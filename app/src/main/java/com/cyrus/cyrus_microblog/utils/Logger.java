package com.cyrus.cyrus_microblog.utils;

import android.util.Log;

import com.cyrus.cyrus_microblog.constants.CommonConstants;

/**
 * 显示log的工具类
 * <p/>
 * Created by Cyrus on 2016/9/2.
 */
public class Logger {

    /**
     * 显示LOG(默认info级别)
     *
     * @param TAG Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void show(String TAG, String msg) {
        if (!CommonConstants.isShowLog) {
            return;
        }
        show(TAG, msg, Log.INFO);
    }

    /**
     * 显示LOG
     *
     * @param TAG   Used to identify the source of a log message.  It usually identifies
     *              the class or activity where the log call occurs.
     * @param msg   The message you would like logged.
     * @param level Log level
     */
    public static void show(String TAG, String msg, int level) {
        if (!CommonConstants.isShowLog) {
            return;
        }
        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG, msg);
                break;
            case Log.DEBUG:
                Log.d(TAG, msg);
                break;
            case Log.INFO:
                Log.i(TAG, msg);
                break;
            case Log.WARN:
                Log.w(TAG, msg);
                break;
            case Log.ERROR:
                Log.e(TAG, msg);
                break;
            default:
                Log.i(TAG, msg);
                break;
        }
    }
}
