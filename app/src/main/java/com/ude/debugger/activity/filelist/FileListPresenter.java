package com.ude.debugger.activity.filelist;

import android.content.Context;

import com.ude.debugger.utils.FileInit;
import com.ude.debugger.utils.ThreadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by ude on 2017-10-12.
 */

public class FileListPresenter {
    private Context context;
    private FileListConstract fileListConstract;
    private ExecutorService executorService;//线程池

    public FileListPresenter(Context context,FileListConstract fileListConstract){
        this.context = context;
        this.fileListConstract = fileListConstract;
        executorService = ThreadUtils.newCachedThreadPool();
    }

    public void openFileDirectory(final String Path){
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
                    if (file!=null ){
                    if (file.listFiles()!=null) {
                        List<String> files = new ArrayList<>();
                        Collections.addAll(files, file.list());
                        if (files.size() == 0) {
                            fileListConstract.onNoFileFound();
                        } else {
                            fileListConstract.onGetFileSuccess(files);
                        }
                    }else {
                        fileListConstract.onNoFileFound();
                    }
                    }
                }else {
                    fileListConstract.onGetFileFail("无文件读写权限");
                }
            }
        });
    }

    /**
     * 释放资源
     */
    public void onDestroy(){
        executorService.shutdownNow();
    }
}
