package com.ude.debuggerlibrary.utils;

import android.util.DisplayMetrics;
import android.view.WindowManager;


/**
 * Created by ude on 2017-09-15.
 */

public class WindowUtils {
    public int width , height;//屏幕宽度和高度
    public static WindowUtils windowUtils;

    private WindowUtils(WindowManager manager){
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;
    }

    public static WindowUtils getInstance(WindowManager manager){
        if (windowUtils == null){
            windowUtils = new WindowUtils(manager);
        }
        return windowUtils;
    }

    public int getWindowWidth(){
        return width;
    }

    public int getWindowHeight(){
        return height;
    }


}
