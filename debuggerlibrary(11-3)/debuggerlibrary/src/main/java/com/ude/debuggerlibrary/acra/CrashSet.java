package com.ude.debuggerlibrary.acra;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ude.debuggerlibrary.R;

import org.acra.ReportingInteractionMode;
import org.acra.collector.CrashReportData;

/**
 * Created by ude on 2017-10-17.
 */

public class CrashSet {
    public static final ReportingInteractionMode TOAST = ReportingInteractionMode.TOAST;//toast模式
    public static final ReportingInteractionMode DIALOG = ReportingInteractionMode.DIALOG;//dialog模式

    private int toastText = R.string.app_error;//崩溃提示内容
    private int dialogText = R.string.crash_dialog_text;//dialog文本提示内容
    private int dialogIcon = android.R.drawable.ic_dialog_info;//dialog提示图片
    private int dialogTitle = R.string.crash_dialog_title;//dialog标题
    private int dialogOkToast = R.string.crash_dialog_ok_toast;//dialog点击确定后的toast提示
    private int dialogCommentPrompt = R.string.crash_dialog_comment_prompt;//dialog输入提示
    private ReportingInteractionMode reportingInteractionMode = ReportingInteractionMode.TOAST;//崩溃提示的模式,默认为Toast提示
    private OnCrashHandler crashHandler;//自定义崩溃处理
    private boolean isUp = true;//是否上传服务器
    private String url;//上传服务器的接口

    public boolean isUp() {
        return isUp;
    }

    public CrashSet setUp(boolean up) {
        isUp = up;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public CrashSet setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getDialogCommentPrompt() {
        return dialogCommentPrompt;
    }

    public CrashSet setDialogCommentPrompt(int dialogCommentPrompt) {
        this.dialogCommentPrompt = dialogCommentPrompt;
        return this;
    }

    public int getDialogText() {
        return dialogText;
    }

    public CrashSet setDialogText(int dialogText) {
        this.dialogText = dialogText;
        return this;
    }

    public int getDialogIcon() {
        return dialogIcon;
    }

    public CrashSet setDialogIcon(int dialogIcon) {
        this.dialogIcon = dialogIcon;
        return this;
    }

    public int getDialogTitle() {
        return dialogTitle;
    }

    public CrashSet setDialogTitle(int dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    public int getDialogOkToast() {
        return dialogOkToast;
    }

    public CrashSet setDialogOkToast(int dialogOkToast) {
        this.dialogOkToast = dialogOkToast;
        return this;
    }

    public int getToastText() {
        return toastText;
    }

    public CrashSet setToastText(int toastText) {
        this.toastText = toastText;
        return this;
    }

    public ReportingInteractionMode getReportingInteractionMode() {
        return reportingInteractionMode;
    }

    public CrashSet setReportingInteractionMode(ReportingInteractionMode reportingInteractionMode) {
        this.reportingInteractionMode = reportingInteractionMode;
        return this;
    }

    public CrashSet setCrashHandler(OnCrashHandler crashHandler) {
        this.crashHandler = crashHandler;
        return this;
    }

    public OnCrashHandler getCrashHandler() {
        return crashHandler;
    }

    /**
     * 自定义崩溃处理的接口
     */
    public interface OnCrashHandler {
        void OnCrash(@NonNull Context context, @NonNull String content, @NonNull CrashReportData errorContent);
    }
}
