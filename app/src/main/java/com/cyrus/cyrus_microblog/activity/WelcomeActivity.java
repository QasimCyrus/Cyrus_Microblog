package com.cyrus.cyrus_microblog.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cyrus.cyrus_microblog.BaseActivity;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.constants.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

public class WelcomeActivity extends BaseActivity {

    private static final int INTENT2MAIN = 1;
    private static final int INTENT2AUTH = 2;
    private static final int DELAY_MILLIS = 2000;

    private Oauth2AccessToken mAccessToken;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case INTENT2MAIN:
                    intent2Activity(MainActivity.class);
                    finish();
                    break;
                case INTENT2AUTH:
                    intent2Activity(AuthActivity.class);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            mHandler.sendEmptyMessageDelayed(INTENT2MAIN, DELAY_MILLIS);
        } else {
            mHandler.sendEmptyMessageDelayed(INTENT2AUTH, DELAY_MILLIS);
        }
    }
}
