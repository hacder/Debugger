package com.ude.debuggerlibrary.activity.search;


import com.ude.debuggerlibrary.data.CheckData;

/**
 * Created by ude on 2017-10-17.
 */

public interface SearchConstract {
    void onSearchSuccess(CheckData checkData);

    void onSearchFail(String msg);

    void onSearchFinish(String msg);

    void onUpMessageFinish(int success, int fails);
}
