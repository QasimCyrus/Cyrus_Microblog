package com.cyrus.cyrus_microblog.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.cyrus.cyrus_microblog.R;

import java.util.List;

public class DialogUtils {

    public static Dialog createLoadingDialog(Context context) {
        return new ProgressDialog(context, R.style.DialogCommon);
    }

    /**
     * 提示信息dialog
     *
     * @param context
     * @param title   标题名称,内容为空时即不设置标题
     * @param msg     提示信息内容
     * @return
     */
    public static AlertDialog showMsgDialog(Context context, String title,
                                            String msg) {
        Builder builder = new Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        AlertDialog dialog = builder.setMessage(msg)
                .setNegativeButton("确定", null).show();
        return dialog;
    }

    /**
     * 确认dialog
     *
     * @param context                上下文
     * @param title                  标题名称,内容为空时即不设置标题
     * @param msg                    确认信息内容
     * @param confirmOnClickListener 确定按钮监听
     * @param cancelOnClickListener  取消按钮监听
     * @return
     */
    public static AlertDialog showConfirmDialog(Context context, String title, String msg,
                                                OnClickListener confirmOnClickListener,
                                                OnClickListener cancelOnClickListener) {
        Builder builder = new Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        AlertDialog dialog = builder.setMessage(msg)
                .setPositiveButton("确认", confirmOnClickListener)
                .setNegativeButton("取消", cancelOnClickListener).show();
        return dialog;
    }

    /**
     * 列表型dialog
     *
     * @param context
     * @param title           标题名称,内容为空时即不设置标题
     * @param items           所有item选项的名称
     * @param onClickListener 确定按钮监听
     * @return
     */
    public static AlertDialog showListDialog(Context context, String title,
                                             List<String> items, OnClickListener onClickListener) {
        return showListDialog(context, title,
                items.toArray(new String[items.size()]), onClickListener);
    }

    /**
     * 列表型dialog
     *
     * @param context
     * @param title           标题名称,内容为空时即不设置标题
     * @param items           所有item选项的名称
     * @param onClickListener 确定按钮监听
     * @return
     */
    public static AlertDialog showListDialog(Context context, String title,
                                             String[] items, OnClickListener onClickListener) {
        Builder builder = new Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        return builder.setItems(items, onClickListener).show();
    }

    /**
     * 单选框dialog
     *
     * @param context
     * @param title            标题名称,内容为空时即不设置标题
     * @param items            所有item选项的名称
     * @param defaultItemIndex 默认选项
     * @param onClickListener  确定按钮监听
     * @return
     */
    public static AlertDialog showSingleChoiceDialog(Context context,
                                                     String title, String[] items, int defaultItemIndex,
                                                     OnClickListener onClickListener) {
        Builder builder = new Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        AlertDialog dialog = builder
                .setSingleChoiceItems(items, defaultItemIndex, onClickListener)
                .setNegativeButton("取消", null).show();
        return dialog;
    }

    /**
     * 复选框对话框
     *
     * @param context
     * @param title                      标题名称,内容为空时即不设置标题
     * @param items                      所有item选项的名称
     * @param defaultCheckedItems        初始化选择,和items同长度,true代表对应位置选中,如{true, true,
     *                                   false}代表第一二项选中,第三项不选中
     * @param onMultiChoiceClickListener 多选监听
     * @param onClickListener            确定按钮监听
     * @return
     */
    public static AlertDialog showMultiChoiceDialog(Context context,
                                                    String title, String[] items, boolean[] defaultCheckedItems,
                                                    OnMultiChoiceClickListener onMultiChoiceClickListener,
                                                    OnClickListener onClickListener) {
        Builder builder = new Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        AlertDialog dialog = builder
                .setMultiChoiceItems(items, defaultCheckedItems,
                        onMultiChoiceClickListener)
                .setPositiveButton("确定", null).setNegativeButton("取消", null)
                .show();
        return dialog;
    }

    /**
     * 选择取照片的方法,结果在activity的onActivityResult()方法中,
     * 利用ImageUtils.getImageOnActivityResult获取
     *
     * @param activity 调用该方法的Activity
     */
    public static void showImagePickDialog(final Activity activity) {
        String title = "选择获取图片的方式";
        String[] items = new String[]{"拍照", "从手机中选择"};
        showListDialog(activity, title, items,
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                ImageUtils.openCameraImage(activity);
                                break;
                            case 1:
                                ImageUtils.openLocalImage(activity);
                                break;
                        }
                    }
                });
    }

    // 显示选择查看软件
    public static void showImage(Context context, Uri imgUri) {
        // 使用Intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Uri mUri = Uri.parse("file://" +
        // picFile.getPath());Android3.0以后最好不要通过该方法，存在一些小Bug
        intent.setDataAndType(imgUri, "image/*");
        context.startActivity(intent);
    }

    // Dialog Builder 模式
    public static class MyBuilder {

        private Context context;

        public MyBuilder(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public MyBuilder setTitle(String title) {
            return this;
        }

        public MyBuilder setMessage(String message) {
            return this;
        }

        public MyBuilder setPositiveButton(String text, final OnClickListener listener) {
            return this;
        }

        public MyBuilder setNegativeButton(String text, final OnClickListener listener) {
            return this;
        }

        public Dialog create() {
            final Dialog dialog = new Dialog(context);
//            P.apply(dialog.mAlert);
//            dialog.setCancelable(P.mCancelable);
//            if (P.mCancelable) {
//                dialog.setCanceledOnTouchOutside(true);
//            }
//            dialog.setOnCancelListener(P.mOnCancelListener);
//            dialog.setOnDismissListener(P.mOnDismissListener);
//            if (P.mOnKeyListener != null) {
//                dialog.setOnKeyListener(P.mOnKeyListener);
//            }
            return dialog;
        }

        public Dialog show() {
            Dialog dialog = create();
            dialog.show();
            return dialog;
        }
    }
}
