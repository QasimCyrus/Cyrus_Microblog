package com.cyrus.cyrus_microblog.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cyrus.cyrus_microblog.activity.MainActivity;

import java.util.ArrayList;

/**
 * 该类用于管理MainActivity的四个Fragment，单例模式
 * <p/>
 * Created by Cyrus on 2016/9/2.
 */
public class FragmentController {

    /**
     * 四个Fragment位置的常量
     */
    public static final int HOME_POSITION = 0;
    public static final int MESSAGE_POSITION = 1;
    public static final int SEARCH_POSITION = 2;
    public static final int USER_POSITION = 3;

    private static FragmentController mFragmentController;

    private int mContainerId;
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragments;

    private FragmentController(MainActivity activity, int containerId) {
        mFragmentManager = activity.getSupportFragmentManager();
        mContainerId = containerId;

        initFragments();
    }

    public static FragmentController getInstance(MainActivity activity, int containerId) {
        if (mFragmentController == null) {
            mFragmentController = new FragmentController(activity, containerId);
        }
        return mFragmentController;
    }

    /**
     * 在MainActivity销毁时调用，否则会因为抛出IllegalStateException异常
     */
    public static void onDestroy() {
        mFragmentController = null;
    }

    /**
     * 初始化Fragment列表，并把Fragment添加到布局中
     */
    private void initFragments() {
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());//HomeFragment - 0
        mFragments.add(new MessageFragment());//MessageFragment - 1
        mFragments.add(new SearchFragment());//SearchFragment - 2
        mFragments.add(new UserFragment());//UserFragment - 3

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            fragmentTransaction.add(mContainerId, fragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * 用于显示单个Fragment
     *
     * @param position Fragment在列表中的位置
     */
    public void showFragment(int position) {
        hideFragment();
        mFragmentManager.beginTransaction()
                .show(mFragments.get(position))
                .commit();
    }

    /**
     * 隐藏所有的Fragment
     */
    private void hideFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            if (fragment != null) {
                fragmentTransaction.hide(fragment);
            }
        }
        fragmentTransaction.commit();
    }

    /**
     * 获得列表中单个的Fragment
     *
     * @param position Fragment在列表中的位置
     * @return 对应position的Fragment对象
     */
    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }
}
