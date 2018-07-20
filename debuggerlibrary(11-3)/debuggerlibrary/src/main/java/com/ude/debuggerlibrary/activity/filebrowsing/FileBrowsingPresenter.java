package com.ude.debuggerlibrary.activity.filebrowsing;

import android.content.Context;

import com.ude.debuggerlibrary.utils.FileInit;
import com.ude.debuggerlibrary.utils.LogUtil;
import com.ude.debuggerlibrary.utils.ThreadUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by ude on 2017-10-12.
 */

public class FileBrowsingPresenter {
    private Context context;
    private FileBrowsingConstract fileBrowsingConstract;
    private ExecutorService executorService;
    private boolean isLoading = false;//是否在加载文件
    private Future<String> future;

    public FileBrowsingPresenter(Context context, FileBrowsingConstract fileBrowsingConstract) {
        this.context = context;
        this.fileBrowsingConstract = fileBrowsingConstract;
        executorService = ThreadUtils.newSingleThreadPool();
    }

    /**
     * 文件是否加载中
     *
     * @return
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 获取文件内容,一行一行的输出
     *
     * @param file
     */
    public void getFileContent(final File file) {
        if (isLoading) {
            return;
        }
        isLoading = true;
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        if (FileInit.getInstance(context).isPermission()) {
            future = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        String content;
                        int num = 0;
                        while ((content = bufferedReader.readLine()) != null) {
                            fileBrowsingConstract.onGetOneFileContent(content);
                            num++;
                        }
                        bufferedReader.close();
                        if (num == 0) {
                            fileBrowsingConstract.onGetFileFail("文件内容为空,点击刷新");
                        }
                    } catch (IOException e) {
                        LogUtil.d(e.getMessage());
                        fileBrowsingConstract.onGetFileFail("打开文件失败,点击刷新");
                    }
                }
            }, "file");
        } else {
            fileBrowsingConstract.onGetFileFail("打开文件失败,请检查是否授予文件读写权限,点击刷新");
        }
        isLoading = false;
    }

    public void getListContentByKry(final List<String> strings, final String key) {
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        if (FileInit.getInstance(context).isPermission()) {
            future = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    if (strings.size() == 0) {
                        fileBrowsingConstract.onGetFileFail("文件为空");
                    } else {
                        boolean isNull = true;
                        for (String content : strings) {
                            if (content.contains(key)) {
                                fileBrowsingConstract.onSearchSuccess(content);
                                isNull = false;
                            }
                        }
                        if (isNull) {
                            fileBrowsingConstract.onGetFileFail("无搜索结果");
                        }
                    }
                }
            }, "search");
        } else {
            fileBrowsingConstract.onGetFileFail("打开文件失败,请检查是否授予文件读写权限,点击刷新");
        }
    }

    public void onDestroy() {
        executorService.shutdownNow();
    }
}
