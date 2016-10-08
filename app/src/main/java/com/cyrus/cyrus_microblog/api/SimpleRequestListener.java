package com.cyrus.cyrus_microblog.api;

import android.app.Dialog;
import android.content.Context;

import com.cyrus.cyrus_microblog.utils.Logger;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

public class SimpleRequestListener implements RequestListener {

    private Context mContext;
    private Dialog progressDialog;

    public SimpleRequestListener(Context context, Dialog progressDialog) {
        this.mContext = context;
        this.progressDialog = progressDialog;
    }

    public void onComplete(String response) {
        onAllDone();
        Logger.show("REQUEST onComplete", response);
    }

    @Override
    public void onWeiboException(WeiboException e) {
        e.printStackTrace();
    }

    public void onAllDone() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
