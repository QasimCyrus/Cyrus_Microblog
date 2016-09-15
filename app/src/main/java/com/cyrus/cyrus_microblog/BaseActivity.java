package com.cyrus.cyrus_microblog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cyrus.cyrus_microblog.constants.CommonConstants;
import com.cyrus.cyrus_microblog.utils.Logger;
import com.cyrus.cyrus_microblog.utils.ToastUtils;

/**
 * 封装了常用方法的基础Activity
 * <p/>
 * Created by Cyrus on 2016/9/2.
 */
public class BaseActivity extends AppCompatActivity {

    protected String TAG;

    protected BaseApplication mBaseApplication;
    protected SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mBaseApplication = (BaseApplication) getApplication();
        mSharedPreferences = getSharedPreferences(CommonConstants.SP_NAME, MODE_PRIVATE);
    }

    /**
     * 无参数跳转
     *
     * @param targetActivity 要跳转到的目标Activity
     */
    protected void intent2Activity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    /**
     * 显示Toast，时长默认为SHORT
     *
     * @param msg 要显示的信息
     */
    protected void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    /**
     * 显示Toast，可设置时长
     *
     * @param msg      显示信息
     * @param duration 显示时长
     */
    protected void showToast(String msg, int duration) {
        ToastUtils.showToast(this, msg, duration);
    }

    /**
     * 显示Log，级别默认为Info
     *
     * @param msg The message you would like logged.
     */
    protected void showLog(String msg) {
        Logger.show(TAG, msg);
    }

    /**
     * 显示log，可设置级别
     *
     * @param msg   The message you would like logged.
     * @param level The message you would like logged.
     */
    protected void showLog(String msg, int level) {
        Logger.show(TAG, msg, level);
    }
}
