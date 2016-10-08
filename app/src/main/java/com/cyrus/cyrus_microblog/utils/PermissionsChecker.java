package com.cyrus.cyrus_microblog.utils;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

/**
 * 检测权限的工具类,
 * 对于Android6.0以上版本有效。
 * <p/>
 * Created by Cyrus on 2016/8/7.
 */
public class PermissionsChecker {

    private Context mContext;

    public PermissionsChecker(Context context) {
        mContext = context;
    }

    /**
     * 用于判断权限集合
     *
     * @param permissions 权限的集合
     * @return 如果缺少权限则返回true
     */
    public boolean lacksPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (lacksPermission(permission)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 用于逐个判断权限的私有函数
     *
     * @param permission 进行判断的单个权限
     * @return 缺少权限则返回true
     */
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission)
                == PermissionChecker.PERMISSION_DENIED;//和PackageManager.PERMISSION_DENIED相等
    }

}
