package com.ude.debugger.activity.filelist;

import java.io.File;
import java.util.List;

/**
 * Created by ude on 2017-10-12.
 */

public interface FileListConstract {
    void onGetFileSuccess(List<String> fileList);
    void onGetFileFail(String msg);
    void onNoFileFound();
}
