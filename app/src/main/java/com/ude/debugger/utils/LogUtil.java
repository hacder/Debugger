package com.ude.debugger.utils;

import android.util.Log;

/**
 * 打印日志的工具类
 *
 * @author yw
 */
public class LogUtil {
    //规定每段显示的长度
    private static int LOG_MAXLENGTH = 2000;
    private static String TAG = "---Debugger---";

    public static void d(String msg) {
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.d(TAG + i, msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.d(TAG, msg.substring(start, strLength));
                break;
            }
        }
    }
}