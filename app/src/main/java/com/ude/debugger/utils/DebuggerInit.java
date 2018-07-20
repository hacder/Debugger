package com.ude.debugger.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.ude.debugger.service.BackService;

/**
 * Created by ude on 2017-10-12.
 */

public class DebuggerInit {
    private static DebuggerInit debuggerInit;//单例
    private Context context;
    private Intent intentService;//后台服务intent
    private FileInit fileInit;//文件初始化及管理器

    public static DebuggerInit getInstance(Context context){
        if (debuggerInit == null){
            debuggerInit = new DebuggerInit(context);
        }
        return debuggerInit;
    }

    private DebuggerInit(Context context){
        this.context = context;
        FileInit.getInstance(context);
        if (askForPermission()) {
            intentService = new Intent(context, BackService.class);
            context.startService(intentService);
        }
    }

    /**
     * 检测是否有悬浮框权限,没有则引导用户开启
     */
    public boolean askForPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)){
                Toast.makeText(context,"无悬浮窗权限,请开启权限",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                        , Uri.parse("package:"+context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            }
        }
        return true;
    }


}
