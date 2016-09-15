package com.cyrus.cyrus_microblog.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.cyrus.cyrus_microblog.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 微博发布时间的显示格式修改工具类。
 * <p/>
 * 发布时间在：
 * （1）10分钟内，显示“刚刚”；
 * （2）1小时内，显示“mm分钟前”；
 * （3）1天内，显示“HH小时前”；
 * （4）前1天，显示“昨天 HH:mm”
 * （5）1年内，显示“MM-dd”；
 * （6）大于1年，显示“yy-MM-dd”。
 */
public class DateUtils {

    /**
     * 一分钟的毫秒数
     */
    public static final long ONE_MINUTE_MILLIONS = 60 * 1000;
    /**
     * 一小时的毫秒数
     */
    public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    /**
     * 一天的毫秒数
     */
    public static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;

    /**
     * 把微博json返回的时间字段格式转换成界面的显示格式
     *
     * @param dateStr 传入的json时间字段格式
     * @return 所要显示的时间格式
     */
    public static String getShortTime(Context context, String dateStr) {
        String str = "";

//        微博json返回时间字段格式："created_at":"Wed Jun 17 14:26:24 +0800 2015"
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        try {
            Date date = sdf.parse(dateStr);//要转换格式的目标时间
            Date curDate = new Date();//当前时间

            //当前时间和目标时间的毫秒差
            long durTime = curDate.getTime() - date.getTime();
            //当前时间和目标时间的日期差
            int dayStatus = calculateDayStatus(date, curDate);

            if (durTime <= ONE_MINUTE_MILLIONS) {
                str = context.getString(R.string.date_just_now);
            } else if (durTime < ONE_HOUR_MILLIONS) {
                str = durTime / ONE_MINUTE_MILLIONS + context.getString(R.string.date_min_ago);
            } else if (dayStatus == 0) {
                str = durTime / ONE_HOUR_MILLIONS + context.getString(R.string.date_hour_ago);
            } else if (dayStatus == -1) {
                str = context.getString(R.string.date_yesterday) + DateFormat.format("HH:mm", date);
            } else if (isSameYear(date, curDate) && dayStatus < -1) {
                str = DateFormat.format("MM-dd", date).toString();
            } else {
                str = DateFormat.format("yyyy-MM", date).toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;
    }

    /**
     * 判断目标时间和比较时间是否在同一年
     *
     * @param targetTime  目标时间
     * @param compareTime 用于比较的时间
     * @return 在同一年则返回true，不同年则返回false
     */
    public static boolean isSameYear(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }

    /**
     * 判断目标时间和比较时间是否在同一天
     *
     * @param targetTime  目标时间
     * @param compareTime 用于比较的时间
     * @return 目标时间和所比较时间的相差天数
     */
    public static int calculateDayStatus(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }
}
