package com.lv.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    /**
     * 把毫秒，转换成 ：。如：03:10
     * @param  millisencond
     * @return String
     */
    public static String convertMilliSecondToMinute2(int millisencond) {
        int oneMinute = 1000 * 60;
        int minutes = millisencond / oneMinute;
        int sencond = (millisencond - minutes * oneMinute) / 1000;
        return getNum(minutes) + ":" + getNum(sencond);
    }

    /**
     * 把秒，转换成 ：03:10
     * @param seconds
     * @return
     */
    public static String convertSecondToMinute2(int seconds) {
        int oneMinute = 60;
        int minutes = seconds / oneMinute;
        int mSecond = seconds - minutes * oneMinute;
        return getNum(minutes) + ":" + getNum(mSecond);
    }

    public static String getNum(int num) {
        if (num >= 10) {
            return "" + num;
        } else {
            return "0" + num;
        }
    }
    public static String TimeStamp2Date(Long timestamp){
        //Long timestamp = Long.parseLong(timestampString)*1000;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
        return date;
    }
    public static long getTimestamp() {
       // Timestamp ts = new Timestamp(System.currentTimeMillis());
        return System.currentTimeMillis();
    }
}
