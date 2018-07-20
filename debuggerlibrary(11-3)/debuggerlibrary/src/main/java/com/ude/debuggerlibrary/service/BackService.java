package com.ude.debuggerlibrary.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ude.debuggerlibrary.service.window.PointWindow;


public class BackService extends Service {
    private PointWindow pointWindow;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pointWindow = PointWindow.getrInstance(getApplication());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        pointWindow.onDestroy();
        super.onDestroy();
    }
}
