package com.cyrus.cyrus_microblog.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrus.cyrus_microblog.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文字转化的工具类
 * <p>
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
    public static SpannableString getSpannableString(
            final Context context, TextView textView, String source) {

        //@用户的正则表达式
        String regExAt = "@[\u4e00-\u9fa5\\w]+";
        //#话题#的正则表达式
        String regExTopic = "#[\u4e00-\u9fa5\\w]+#";
        //[表情]的正则表达式
        String regExEmotion = "\\[[\u4e00-\u9fa5\\w]+\\]";
        //@用户、#话题#、[表情]合并的正则表达式
        String regEx = "(" + regExAt + ")|(" + regExTopic + ")|(" + regExEmotion + ")";

        //转化为拓展性字符串
        SpannableString spannableString = new SpannableString(source);

        //用于匹配正则表达式
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(spannableString);

        //有匹配的文字则为textView添加链接功能
        if (matcher.find()) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());

            matcher.reset();
        }

        //循环遍历matcher
        while (matcher.find()) {
            //group(int group)的参数指明匹配哪一个括号
            final String atStr = matcher.group(1);
            final String topicStr = matcher.group(2);
            String emotionStr = matcher.group(3);

            //修改字体为蓝色可点击
            if (atStr != null) {
                int start = matcher.start(1);

                WBClickableSpan clickableSpan = new WBClickableSpan(context) {
                    @Override
                    public void onClick(View widget) {
                        ToastUtils.showToast(context, atStr, Toast.LENGTH_SHORT);
                    }
                };
                spannableString.setSpan(clickableSpan, start, start + atStr.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (topicStr != null) {
                int start = matcher.start(2);

                WBClickableSpan clickableSpan = new WBClickableSpan(context) {
                    @Override
                    public void onClick(View widget) {
                        ToastUtils.showToast(context, topicStr, Toast.LENGTH_SHORT);
                    }
                };
                spannableString.setSpan(clickableSpan, start, start + topicStr.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            //修改字体为表情
            if (emotionStr != null) {
                int start = matcher.start(3);

                int id = EmotionUtils.getImgByName(emotionStr);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);

                if (bitmap != null) {
                    int size = (int) textView.getTextSize() + 1;
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

                    ImageSpan imageSpan = new ImageSpan(context, bitmap);
                    spannableString.setSpan(imageSpan, start, start + emotionStr.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        return spannableString;
    }

    static class WBClickableSpan extends ClickableSpan {

        private Context mContext;

        public WBClickableSpan(Context context) {
            mContext = context;
        }

        @Override
        public void onClick(View widget) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(mContext, R.color.txt_at_blue));
            ds.setUnderlineText(false);//取消下划线
        }
    }
}
