package com.ude.debuggerlibrary.acra;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ude.debuggerlibrary.utils.DebuggerInit;
import com.ude.debuggerlibrary.utils.FileInit;
import com.ude.debuggerlibrary.utils.FunctionSwitch;
import com.ude.debuggerlibrary.utils.LogUtil;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.sender.SenderService;

import okhttp3.MultipartBody;


/**
 * Created by ude on 2017-07-28.
 */

public class MyKeyStoreFactory implements ReportSender {


    /**
     * Send crash report data.
     * <p>
     * Method will be called from the {@link SenderService}.
     *
     * @param context      Android Context in which to send the crash report.
     * @param errorContent Stores key/value pairs for each report field.
     *                     A report field is identified by a {@link ReportField} enum value.
     * @throws ReportSenderException If anything goes fatally wrong during the handling of crash
     *                               data, you can (should) throw a {@link ReportSenderException}
     *                               with a custom message.
     */
    @Override
    public void send(@NonNull final Context context, @NonNull CrashReportData errorContent) throws ReportSenderException {
        final DebuggerInit debuggerInit = DebuggerInit.getInstance(context, true);
        String content = "------------start-----------------\n"
                + errorContent.getProperty(ReportField.USER_CRASH_DATE) + "\n"
                + errorContent.getProperty(ReportField.BUILD) + "\n"
                + errorContent.getProperty(ReportField.DUMPSYS_MEMINFO) + "\n"
                + errorContent.getProperty(ReportField.STACK_TRACE) + "\n";

        if (debuggerInit != null) {
            if (debuggerInit.getCrashSet().getReportingInteractionMode() == ReportingInteractionMode.DIALOG) {
                content = content + "用户反馈信息:" + errorContent.getProperty(ReportField.USER_COMMENT) + "\n";
            }
        }
        content = content + "------------end-----------------\n";
        if (FunctionSwitch.getCrash() == 2) {
            MultipartBody.Builder requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("line",content);

            if (debuggerInit != null) {
                if (debuggerInit.getCrashSet().isUp() && debuggerInit.getCrashSet().getUrl() != null) {
                    debuggerInit.getOkhttpCrashConnect().httpPost(debuggerInit.getCrashSet().getUrl(), requestBody.build());
                    debuggerInit.getOkhttpCrashConnectDef().httpPost(DebuggerInit.URL_CRASH, requestBody.build());
                }
                if (debuggerInit.getCrashSet().getCrashHandler() != null) {//自定义操作
                    debuggerInit.getCrashSet().getCrashHandler().OnCrash(context, content, errorContent);
                }
            }
        }
        if (debuggerInit != null && FunctionSwitch.getCrashSave() == 2) {
            FileInit.getInstance(context).writerContentToCrashFile(content);
        }

        LogUtil.d("崩溃报告:" + content);
    }
}
