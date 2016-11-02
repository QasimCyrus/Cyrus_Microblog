package com.cyrus.cyrus_microblog.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyrus.cyrus_microblog.BaseFragment;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.utils.TitleBarBuilder;

/**
 * 展示消息列表，依附于MainActivity上
 * <p>
 * Created by Cyrus on 2016/9/2.
 */
public class MessageFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        //TitleBar的创建
        initTitleBar(view);

        return view;
    }

    private void initTitleBar(View view) {
        new TitleBarBuilder(view)
                .setTitleText("消息");
    }

}
