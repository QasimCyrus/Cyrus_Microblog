package com.cyrus.cyrus_microblog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.cyrus.cyrus_microblog.activity.MainActivity;

/**
 * Created by Cyrus on 2016/9/2.
 */
public class BaseFragment extends Fragment {

    protected String TAG;
    protected MainActivity mMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();
        TAG = this.getClass().getSimpleName();
    }

    protected void intent2Activity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(mMainActivity, targetActivity);
        startActivity(intent);
    }

    protected MainActivity getMainActivity() {
        return mMainActivity;
    }
}
