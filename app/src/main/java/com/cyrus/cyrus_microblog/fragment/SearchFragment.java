package com.cyrus.cyrus_microblog.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyrus.cyrus_microblog.BaseFragment;
import com.cyrus.cyrus_microblog.R;

/**
 * Created by Cyrus on 2016/9/2.
 */
public class SearchFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

}