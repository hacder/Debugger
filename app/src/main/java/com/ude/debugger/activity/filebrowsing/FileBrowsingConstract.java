package com.ude.debugger.activity.filebrowsing;

/**
 * Created by ude on 2017-10-12.
 */

public interface FileBrowsingConstract {
    void onGetOneFileContent(String content);
    void onGetFileFail(String msg);
    void onSearchSuccess(String content);
}
