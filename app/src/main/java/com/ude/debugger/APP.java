package com.ude.debugger;

import android.app.Application;

import com.ude.debugger.utils.DebuggerInit;

/**
 * Created by ude on 2017-10-10.
 */

public class APP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebuggerInit.getInstance(this);
    }
}
