package com.ude.debuggerlibrary.utils;

/**
 * Created by ude on 2017-11-01.
 */

public class FunctionSwitch {
    public static int Debugger = 2;//debuger调试功能
    public static int LogShow = 2;//log显示功能
    public static int LogSave = 2;//log保存功能
    public static int Res = 2;//资源监视功能
    public static int Crash = 2;//崩溃处理功能
    public static int CrashSave = 2;//崩溃信息保存

    public static void setData(String key,int value){
        if (key.equals("调试功能")) {
            Debugger = value;
        }else if (key.equals("Log信息显示功能")) {
            LogShow = value;
        }else if (key.equals("Log信息自动记录到文件")) {
            LogSave = value;
        }else if (key.equals("崩溃处理功能")) {
            Crash = value;
        }else if (key.equals("崩溃信息自动记录到文件")) {
            CrashSave = value;
        }else if (key.equals("资源监视功能")) {
            Res = value;
        }
    }

    public static int getDebugger() {
        return Debugger;
    }

    public static void setDebugger(int debugger) {
        Debugger = debugger;
    }

    public static int getLogShow() {
        return LogShow;
    }

    public static void setLogShow(int logShow) {
        LogShow = logShow;
    }

    public static int getLogSave() {
        return LogSave;
    }

    public static void setLogSave(int logSave) {
        LogSave = logSave;
    }

    public static int getRes() {
        return Res;
    }

    public static void setRes(int res) {
        Res = res;
    }

    public static int getCrash() {
        return Crash;
    }

    public static void setCrash(int crash) {
        Crash = crash;
    }

    public static int getCrashSave() {
        return CrashSave;
    }

    public static void setCrashSave(int crashSave) {
        CrashSave = crashSave;
    }
}
