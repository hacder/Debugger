package com.ude.debuggerlibrary.activity.filelist;


import com.ude.debuggerlibrary.data.CheckData;

import java.util.List;

/**
 * Created by ude on 2017-10-12.
 */

public interface FileListConstract {
    void onGetFileSuccess(List<String> fileList);

    void onGetFileFail(String msg);

    void onNoFileFound();

    void onSearchSuccess(CheckData checkData);

    void onUpMessageFinish(int success, int fails);

}
