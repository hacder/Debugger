package com.ude.debuggerlibrary.activity.filelist;

import android.content.Context;

import com.ude.debuggerlibrary.data.CheckData;
import com.ude.debuggerlibrary.utils.DebuggerInit;
import com.ude.debuggerlibrary.utils.FileInit;
import com.ude.debuggerlibrary.utils.OkhttpConnect;
import com.ude.debuggerlibrary.utils.ThreadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ude on 2017-10-12.
 */

public class FileListPresenter {
    private Context context;
    private FileListConstract fileListConstract;
    private ExecutorService executorService;//线程池
    private Future<String> future;
    private int success, fails;

    public FileListPresenter(Context context, FileListConstract fileListConstract) {
        this.context = context;
        this.fileListConstract = fileListConstract;
        executorService = ThreadUtils.newCachedThreadPool();
    }

    public void openFileDirectory(final String Path) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (FileInit.getInstance(context).isPermission()) {
                    File file = null;
                    if (Path.equals("Log")) {
                        file = new File(FileInit.getInstance(context).getFILE_LOG());
                    } else if (Path.equals("Crash")) {
                        file = new File(FileInit.getInstance(context).getFILE_CRASH());
                    }
                    if (file != null) {
                        if (file.listFiles() != null) {
                            List<String> files = new ArrayList<>();
                            Collections.addAll(files, file.list());
                            if (files.size() == 0) {
                                fileListConstract.onNoFileFound();
                            } else {
                                fileListConstract.onGetFileSuccess(files);
                            }
                        } else {
                            fileListConstract.onNoFileFound();
                        }
                    }
                } else {
                    fileListConstract.onGetFileFail("无文件读写权限");
                }
            }
        });
    }

    public void upMessage(final List<CheckData> checkDatas) {
        if (FileInit.getInstance(context).isPermission()) {
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
            future = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    success = 0;
                    fails = 0;
                    OkhttpConnect.CallBackAction callBackAction = new OkhttpConnect.CallBackAction() {
                        @Override
                        public void onSuccess(Object model) {
                            success++;
                        }

                        @Override
                        public void onFail(String msg) {
                            fails++;
                        }
                    };
                    for (CheckData data : checkDatas) {
                        if (data.isFile()) {
                            MultipartBody.Builder requestBody = new MultipartBody.Builder();
                            if (data.getFileName().startsWith("LOG")) {
                                requestBody.setType(DebuggerInit.getInstance(context, false).getFileLogOkhttpSet().getMediaTypeFile())
                                        .addFormDataPart("file", data.getFileName()
                                                , RequestBody.create(MultipartBody.FORM
                                                        , new File(FileInit.getInstance(context).getFILE_LOG() + data.getFileName())));
                                DebuggerInit.getInstance(context, false).getOkhttpFileLogConnect()
                                        .httpPost(FileInit.getInstance(context).getUrlLog(), requestBody.build(), false, callBackAction);
                                DebuggerInit.getInstance(context, false).getOkhttpFileLogConnectDef()
                                        .httpPost(DebuggerInit.URL_LOGFILE, requestBody.build(), false, callBackAction);
                            } else {
                                requestBody.setType(DebuggerInit.getInstance(context, false).getFileCrashOkhttpSet().getMediaTypeFile())
                                        .addFormDataPart("file", data.getFileName()
                                                , RequestBody.create(MultipartBody.FORM
                                                        , new File(FileInit.getInstance(context).getFILE_CRASH() + data.getFileName())));
                                DebuggerInit.getInstance(context, false).getOkhttpFileCrashConnect()
                                        .httpPost(FileInit.getInstance(context).getUrlCrash(), requestBody.build(), false, callBackAction);
                                DebuggerInit.getInstance(context, false).getOkhttpFileCrashConnectDef()
                                        .httpPost(DebuggerInit.URL_CRASHFILE, requestBody.build(), false, callBackAction);
                            }
                        } else {
                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("line", data.getContent())
                                    .build();
                            if (data.getFileName().startsWith("LOG")) {
                                DebuggerInit.getInstance(context, false).getOkhttpLogConnect()
                                        .httpPost(
                                                FileInit.getInstance(context).getUrlLogMessage()
                                                , requestBody, false, callBackAction);
                                DebuggerInit.getInstance(context, false).getOkhttpLogConnectDef()
                                        .httpPost(
                                                DebuggerInit.URL_LOG
                                                , requestBody, false, callBackAction);
                            } else {
                                DebuggerInit.getInstance(context, false).getOkhttpCrashConnect()
                                        .httpPost(FileInit.getInstance(context).getUrlLogMessage(), requestBody, false, callBackAction);
                                DebuggerInit.getInstance(context, false).getOkhttpCrashConnectDef()
                                        .httpPost(DebuggerInit.URL_CRASH, requestBody, false, callBackAction);
                            }
                        }
                    }
                    while (success + fails != checkDatas.size()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    fileListConstract.onUpMessageFinish(success, fails);

                }
            }, "up");
        } else {
            fileListConstract.onGetFileFail("请给予文件读写的权限");
        }
    }

    /**
     * 释放资源
     */
    public void onDestroy() {
        executorService.shutdownNow();
    }
}
