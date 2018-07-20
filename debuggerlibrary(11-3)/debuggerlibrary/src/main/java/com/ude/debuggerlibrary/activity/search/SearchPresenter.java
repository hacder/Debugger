package com.ude.debuggerlibrary.activity.search;

import android.content.Context;

import com.ude.debuggerlibrary.data.CheckData;
import com.ude.debuggerlibrary.utils.DebuggerInit;
import com.ude.debuggerlibrary.utils.FileInit;
import com.ude.debuggerlibrary.utils.OkhttpConnect;
import com.ude.debuggerlibrary.utils.ThreadUtils;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ude.debuggerlibrary.adpter.InfoShowAdapter.E;
import static com.ude.debuggerlibrary.adpter.InfoShowAdapter.I;
import static com.ude.debuggerlibrary.adpter.InfoShowAdapter.V;
import static com.ude.debuggerlibrary.adpter.InfoShowAdapter.W;
import static com.ude.debuggerlibrary.service.window.LogcatWindow.D;

/**
 * Created by ude on 2017-10-17.
 */

public class SearchPresenter {
    private Context context;
    private SearchConstract searchConstract;
    private ExecutorService executorService;
    private Future<String> futureName, futureContent, futureUp;
    private int success = 0, fails = 0;//上传成功与失败的记录

    public SearchPresenter(SearchActivity context) {
        this.context = context;
        this.searchConstract = context;
        executorService = ThreadUtils.newCachedThreadPool();
    }

    public void searchKeyByFileName(final String key, final int time) {
        if (FileInit.getInstance(context).isPermission()) {
            futureName = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    long nowTime = System.currentTimeMillis();
                    File[] files = new File(FileInit.getInstance(context).getFILE_LOG()).listFiles();
                    for (File file : files) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            return;
                        }
                        if (file.getName().contains(key) && ((nowTime - file.lastModified()) / 1000 / 86400 < time || time == 0)) {
                            searchConstract.onSearchSuccess(new CheckData(file.getName(), file.getName(), true, false, 0));
                        }
                    }
                    files = new File(FileInit.getInstance(context).getFILE_CRASH()).listFiles();
                    for (File file : files) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            return;
                        }
                        if (file.getName().contains(key) && ((nowTime - file.lastModified()) / 1000 / 86400 < time || time == 0)) {
                            searchConstract.onSearchSuccess(new CheckData(file.getName(), file.getName(), true, false, 0));
                        }
                    }
                    searchConstract.onSearchFinish("文件");
                }
            }, "name");

        } else {
            searchConstract.onSearchFail("请给予文件读写的权限");
        }
    }

    public void searchKeyByFileContent(final String key, final int time, final int level) {
        if (FileInit.getInstance(context).isPermission()) {
            futureContent = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    long nowTime = System.currentTimeMillis();
                    File[] files = new File(FileInit.getInstance(context).getFILE_LOG()).listFiles();
                    for (File file : files) {
                        if (time == 0 || (nowTime - file.lastModified()) / 1000 / 86400 < time) {
                            try {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                                String content;
                                int where = 1;
                                while ((content = bufferedReader.readLine()) != null) {
                                    if (content.contains(key)) {
                                        int lev = V;
                                        if (content.contains(" V ")) {//白,默认
                                            lev = V;
                                        } else if (content.contains(" I ")) {//蓝
                                            lev = I;
                                        } else if (content.contains(" D ")) {//绿
                                            lev = D;
                                        } else if (content.contains(" W ")) {//橙
                                            lev = W;
                                        } else if (content.contains(" E ")) {//红
                                            lev = E;
                                        }
                                        if (lev >= level) {
                                            searchConstract.onSearchSuccess(new CheckData(content, file.getName(), false, false, where));
                                        }
                                    }
                                    where++;
                                }
                                bufferedReader.close();
                            } catch (IOException e) {
                                searchConstract.onSearchFail("打开文件失败");
                            }
                        }
                    }
                    files = new File(FileInit.getInstance(context).getFILE_CRASH()).listFiles();
                    for (File file : files) {
                        if (time == 0 || (nowTime - file.lastModified()) / 1000 / 86400 < time) {
                            try {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                                String content;
                                int where = 1;
                                while ((content = bufferedReader.readLine()) != null) {
                                    if (content.contains(key)) {
                                        int lev = V;
                                        if (content.contains(" V ")) {//白,默认
                                            lev = V;
                                        } else if (content.contains(" I ")) {//蓝
                                            lev = I;
                                        } else if (content.contains(" D ")) {//绿
                                            lev = D;
                                        } else if (content.contains(" W ")) {//橙
                                            lev = W;
                                        } else if (content.contains(" E ")) {//红
                                            lev = E;
                                        }
                                        if (lev >= level) {
                                            searchConstract.onSearchSuccess(new CheckData(content, file.getName(), false, false, where));
                                        }
                                    }
                                    where++;
                                }
                                bufferedReader.close();
                            } catch (IOException e) {
                                searchConstract.onSearchFail("打开文件失败");
                            }
                        }
                    }
                    searchConstract.onSearchFinish("文件内容");
                }
            }, "content");

        } else {
            searchConstract.onSearchFail("请给予文件读写的权限");
        }
    }

    public void upMessage(final List<CheckData> checkDatas) {
        if (FileInit.getInstance(context).isPermission()) {
            futureUp = executorService.submit(new Runnable() {
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
                            List<String> list = new ArrayList<String>();
                            list.add(data.getContent());
                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("line", new JSONArray(list).toString())
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
                    searchConstract.onUpMessageFinish(success, fails);

                }
            }, "up");
        } else {
            searchConstract.onSearchFail("请给予文件读写的权限");
        }
    }

    /**
     * 关闭运行中的线程
     */
    public void cancelTask() {
        if (futureName != null && !futureName.isDone()) {
            futureName.cancel(true);
        }
        if (futureContent != null && !futureContent.isDone()) {
            futureContent.cancel(true);
        }
    }

    public void onDestroy() {
    }
}
