package com.cyrus.cyrus_microblog.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.cyrus.cyrus_microblog.BaseActivity;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.fragment.FragmentController;

public class MainActivity extends BaseActivity {

    private static FragmentController mFragmentController;

    private RadioGroup mRadioGroup;
    private ImageView mIvAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentController = FragmentController.getInstance(this, R.id.fl_container);
        mFragmentController.showFragment(FragmentController.HOME_POSITION);

        mRadioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        mFragmentController.showFragment(FragmentController.HOME_POSITION);
                        break;
                    case R.id.rb_message:
                        mFragmentController.showFragment(FragmentController.MESSAGE_POSITION);
                        break;
                    case R.id.rb_search:
                        mFragmentController.showFragment(FragmentController.SEARCH_POSITION);
                        break;
                    case R.id.rb_user:
                        mFragmentController.showFragment(FragmentController.USER_POSITION);
                        break;
                }
            }
        });

        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FragmentController.onDestroy();
    }
}
