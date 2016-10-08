package com.cyrus.cyrus_microblog.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.activity.UserInfoActivity;
import com.cyrus.cyrus_microblog.model.Emotion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文字转化的工具类
 * <p/>
 * Created by Cyrus on 2016/9/5.
 */
public class StringUtils {

    /**
     * 将匹配的文字返回成拓展性字符串：
     * 1.@用户：转换为可点击文字
     * 2.#话题#：转换成可点击文字
     * 3.[表情]：转换成表情图片
     *
     * @return 转换之后的拓展性字符串
     */
    private static SpannableString getSpannableString(
            final Context context, final TextView textView, String source, boolean isClickable) {

        //转化为拓展性字符串
        SpannableString spannableString = new SpannableString(source);
        //获取资源
        Resources resources = context.getResources();

        //@用户的正则表达式
        String regExAt = "@[\u4e00-\u9fa5\\w]+";
        //#话题#的正则表达式
        String regExTopic = "#[\u4e00-\u9fa5\\w]+#";
        //@用户、#话题#的正则表达式
        String regLink = "(" + regExAt + ")|(" + regExTopic + ")";

        //可点击字体的匹配
        Pattern patternLink = Pattern.compile(regLink);
        Matcher matcherLink = patternLink.matcher(spannableString);

        //[表情]的正则表达式
        String regExEmotion = "\\[[\u4e00-\u9fa5\\w]+\\]";

        //表情的匹配
        Pattern patternEmotion = Pattern.compile(regExEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        //有匹配的文字则为textView添加链接功能
        if (matcherLink.find() && isClickable) {
            textView.setMovementMethod(LinkTouchMovementMethod.getInstance());
            matcherLink.reset();
        }

        //循环遍历matcher
        while (true) {
            if (matcherLink.find()) {
                final String linkStr = matcherLink.group();//获取匹配的字符串
                int start = matcherLink.start();//匹配字符串开始位置

                if (isClickable) {
                    TouchableSpan touchableSpan = new TouchableSpan(
                            ContextCompat.getColor(context, R.color.txt_at_blue),
                            ContextCompat.getColor(context, R.color.txt_at_blue),
                            ContextCompat.getColor(context, R.color.bg_at_blue)) {
                        @Override
                        public void onClick(View widget) {
                            if (linkStr.startsWith("@")) {
                                Intent intent = new Intent(context, UserInfoActivity.class);
                                intent.putExtra("mUserName", linkStr.substring(1));
                                context.startActivity(intent);
                            } else if (linkStr.startsWith("#")) {
                                ToastUtils.showToast(context, "查看话题：" + linkStr,
                                        Toast.LENGTH_SHORT);
                            } else if (textView.getParent() instanceof LinearLayout) {
                                ((LinearLayout) textView.getParent()).performClick();
                            }
                        }
                    };
                    spannableString.setSpan(touchableSpan, start, start + linkStr.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    int blueColor = ContextCompat.getColor(context, R.color.txt_at_blue);
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(blueColor);
                    spannableString.setSpan(colorSpan, start, start + linkStr.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else if (matcherEmotion.find()) {
                String emotionStr = matcherEmotion.group();
                int start = matcherEmotion.start();

                Integer imgRes = Emotion.getImgByName(emotionStr);
                if (imgRes != -1) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //这里只是为了设置options的参数
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(resources, imgRes, options);

                    int scale = (int) (options.outWidth / 32.0);
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = scale;
                    Bitmap bitmap = BitmapFactory.decodeResource(resources, imgRes, options);

                    ImageSpan span = new ImageSpan(context, bitmap);
                    spannableString.setSpan(span, start, start + emotionStr.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                break;
            }
        }

        return spannableString;
    }

    public static SpannableString getSpannableString(
            final Context context, TextView textView, String source) {
        return getSpannableString(context, textView, source, true);
    }

    private static abstract class TouchableSpan extends ClickableSpan {
        private boolean mIsPressed;
        private int mPressedBackgroundColor;
        private int mNormalTextColor;
        private int mPressedTextColor;

        TouchableSpan(int normalTextColor, int pressedTextColor,
                      int pressedBackgroundColor) {
            mNormalTextColor = normalTextColor;
            mPressedTextColor = pressedTextColor;
            mPressedBackgroundColor = pressedBackgroundColor;
        }

        void setPressed(boolean isSelected) {
            mIsPressed = isSelected;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
            ds.bgColor = mIsPressed ? mPressedBackgroundColor : Color.TRANSPARENT;
            ds.setUnderlineText(false);
        }
    }

    private static class LinkTouchMovementMethod extends LinkMovementMethod {
        private TouchableSpan mPressedSpan;

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
                            spannable.getSpanEnd(mPressedSpan));
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            } else {
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return false;
        }

        private TouchableSpan getPressedSpan(TextView textView, Spannable spannable,
                                             MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
            TouchableSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        }
    }

}
