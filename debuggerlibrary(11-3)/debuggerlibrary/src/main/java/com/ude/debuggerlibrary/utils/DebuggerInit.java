package com.ude.debuggerlibrary.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Toast;

import com.ude.debuggerlibrary.acra.CrashSenderFactory;
import com.ude.debuggerlibrary.acra.CrashSet;
import com.ude.debuggerlibrary.service.BackService;

import org.acra.ACRA;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.HttpSender;

import static com.ude.debuggerlibrary.utils.FunctionSwitch.setCrash;

/**
 * Created by ude on 2017-10-12.
 */

public class DebuggerInit {
    private static DebuggerInit debuggerInit;//单例
    public static final String URL_LOG = "/Api/Log/line";
    public static final String URL_LOGFILE = "/Api/Log/line";
    public static final String URL_CRASH = "/Api/Log/line";
    public static final String URL_CRASHFILE = "/Api/Log/line";
    private Context context;
    private Application application;
    private Intent intentService;//后台服务intent
    private FileInit fileInit;//文件初始化及管理器
    private OkhttpSet fileLogOkhttpSet,fileCrashOkhttpSet,crashOkhttpSet,logOkhttpSet;//网络设置
    private OkhttpConnect okhttpFileLogConnect,okhttpCrashConnect,okhttpFileCrashConnect,okhttpLogConnect;//网络连接
    private CrashSet crashSet;//崩溃处理设置
    private boolean isDefEnable = false;

    private OkhttpSet fileLogOkhttpSetDef,fileCrashOkhttpSetDef,crashOkhttpSetDef,logOkhttpSetDef;//默认网络设置
    private OkhttpConnect okhttpFileLogConnectDef,okhttpCrashConnectDef,okhttpFileCrashConnectDef,okhttpLogConnectDef;//默认网络连接

    public static DebuggerInit getInstance(Application application){
        if (debuggerInit == null){
            debuggerInit = new DebuggerInit(application);
        }
        return debuggerInit;
    }

    public static DebuggerInit getInstance(Context context,boolean get){
        if (debuggerInit == null){
            return  null;
        }
        return debuggerInit;
    }

    private DebuggerInit(Application application){
        this.application = application;
        this.context = application;
        ToastUtils.init(context);
        crashSet = new CrashSet();
        fileLogOkhttpSet = new OkhttpSet();
        fileCrashOkhttpSet = new OkhttpSet();
        crashOkhttpSet = new OkhttpSet();
        logOkhttpSet = new OkhttpSet();
        fileLogOkhttpSetDef = new OkhttpSet();
        fileCrashOkhttpSetDef = new OkhttpSet();
        crashOkhttpSetDef = new OkhttpSet();
        logOkhttpSetDef = new OkhttpSet();
        fileInit = FileInit.getInstance(context);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        FunctionSwitch.setDebugger(sp.getInt("调试功能",2));
        FunctionSwitch.setLogShow(sp.getInt("Log信息显示功能",2));
        FunctionSwitch.setLogSave(sp.getInt("Log信息自动记录到文件",2));
        setCrash(sp.getInt("崩溃处理功能",2));
        FunctionSwitch.setCrashSave(sp.getInt("崩溃信息自动记录到文件",2));
        FunctionSwitch.setRes(sp.getInt("资源监视功能",2));

    }

    /**
     * 初始化
     */
    public void init(){

        //崩溃处理初始化
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder(application)
                .setReportSenderFactoryClasses(CrashSenderFactory.class)
                .setReportType(HttpSender.Type.JSON);//发送消息的类型

        configurationBuilder.setReportingInteractionMode(crashSet.getReportingInteractionMode())//崩溃提示模式
                .setResToastText(crashSet.getToastText())//崩溃提示内容
                .setResDialogText(crashSet.getToastText())//dialog文本提示内容
                .setResDialogIcon(crashSet.getDialogIcon())//dialog提示图片
                .setResDialogTitle(crashSet.getDialogTitle())//dialog标题
                .setResDialogCommentPrompt(crashSet.getDialogCommentPrompt())//dialog输入提示
                .setResDialogOkToast(crashSet.getDialogOkToast());//dialog点击确定后的toast提示

        try {
            ACRA.init(application,configurationBuilder.build());
        } catch (ACRAConfigurationException e) {
            e.printStackTrace();
        }
        //文件存储初始化
        fileInit.init();

        if (isDefEnable){
            initDef();
        }
        //网络初始化
        okhttpCrashConnect = new OkhttpConnect(crashOkhttpSet,context);
        okhttpFileCrashConnect = new OkhttpConnect(fileCrashOkhttpSet,context);
        okhttpFileLogConnect = new OkhttpConnect(fileLogOkhttpSet,context);
        okhttpLogConnect = new OkhttpConnect(logOkhttpSet,context);

        //窗口初始化
        if (askForPermission()) {
            intentService = new Intent(context, BackService.class);
            context.startService(intentService);
        }
    }

    /**
     * 初始化默认上传的配置
     */
    public void initDef(){
        fileLogOkhttpSetDef.setHOST("http://apiwiki.hiiyun.com");
        fileCrashOkhttpSetDef.setHOST("http://apiwiki.hiiyun.com");
        crashOkhttpSetDef.setHOST("http://apiwiki.hiiyun.com");
        logOkhttpSetDef.setHOST("http://apiwiki.hiiyun.com");
        okhttpFileLogConnectDef = new OkhttpConnect(fileLogOkhttpSetDef,context);
        okhttpCrashConnectDef = new OkhttpConnect(crashOkhttpSetDef,context);
        okhttpFileCrashConnectDef = new OkhttpConnect(fileCrashOkhttpSetDef,context);
        okhttpLogConnectDef = new OkhttpConnect(logOkhttpSetDef,context);
    }

    /**
     * 设置项目的密钥
     * @param app 项目标识
     * @param appkey
     */
    public void setDefEnable(String app,String appkey){
        isDefEnable = true;
        DefIntercepterUtils defIntercepterUtils = new DefIntercepterUtils(app,appkey);
        fileLogOkhttpSetDef.setInterceptorHeader(defIntercepterUtils.getmInterceptor());
        fileCrashOkhttpSetDef.setInterceptorHeader(defIntercepterUtils.getmInterceptor());
        crashOkhttpSetDef.setInterceptorHeader(defIntercepterUtils.getmInterceptor());
        logOkhttpSetDef.setInterceptorHeader(defIntercepterUtils.getmInterceptor());
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

    /**
     * 主动释放资源
     */
    public void onDestroy(){
        if (intentService != null) {
            context.stopService(intentService);//关闭小窗口
        }
    }

    public OkhttpConnect getOkhttpFileLogConnectDef() {
        return okhttpFileLogConnectDef;
    }

    public OkhttpConnect getOkhttpCrashConnectDef() {
        return okhttpCrashConnectDef;
    }

    public OkhttpConnect getOkhttpFileCrashConnectDef() {
        return okhttpFileCrashConnectDef;
    }

    public OkhttpConnect getOkhttpLogConnectDef() {
        return okhttpLogConnectDef;
    }

    public OkhttpSet getLogOkhttpSetDef() {
        return logOkhttpSetDef;
    }

    public DebuggerInit setLogOkhttpSetDef(OkhttpSet logOkhttpSetDef) {
        this.logOkhttpSetDef = logOkhttpSetDef;
        return this;
    }

    public OkhttpSet getFileLogOkhttpSetDef() {
        return fileLogOkhttpSetDef;
    }

    public DebuggerInit setFileLogOkhttpSetDef(OkhttpSet fileLogOkhttpSetDef) {
        this.fileLogOkhttpSetDef = fileLogOkhttpSetDef;
        return this;
    }

    public OkhttpSet getFileCrashOkhttpSetDef() {
        return fileCrashOkhttpSetDef;
    }

    public DebuggerInit setFileCrashOkhttpSetDef(OkhttpSet fileCrashOkhttpSetDef) {
        this.fileCrashOkhttpSetDef = fileCrashOkhttpSetDef;
        return this;
    }

    public OkhttpSet getCrashOkhttpSetDef() {
        return crashOkhttpSetDef;
    }

    public DebuggerInit setCrashOkhttpSetDef(OkhttpSet crashOkhttpSetDef) {
        this.crashOkhttpSetDef = crashOkhttpSetDef;
        return this;
    }

    public DebuggerInit setDebuggerEnable(boolean isEnable){
        if (isEnable) {
            FunctionSwitch.setDebugger(2);
        }else {
            FunctionSwitch.setDebugger(1);
        }
        return this;
    }
    public DebuggerInit setLogShowEnable(boolean isEnable){
        if (isEnable) {
            FunctionSwitch.setLogShow(2);
        }else {
            FunctionSwitch.setLogShow(1);
        }
        return this;
    }
    public DebuggerInit setLogSaveEnable(boolean isEnable){
        if (isEnable) {
            FunctionSwitch.setLogSave(2);
        }else {
            FunctionSwitch.setLogSave(1);
        }
        return this;
    }
    public DebuggerInit setCrashEnable(boolean isEnable){
        if (isEnable) {
            FunctionSwitch.setCrash(2);
        }else {
            FunctionSwitch.setCrash(1);
        }
        return this;
    }
    public DebuggerInit setCrashSaveEnable(boolean isEnable){
        if (isEnable) {
            FunctionSwitch.setCrashSave(2);
        }else {
            FunctionSwitch.setCrashSave(1);
        }
        return this;
    }
    public DebuggerInit setResEnable(boolean isEnable){
        if (isEnable) {
            FunctionSwitch.setRes(2);
        }else {
            FunctionSwitch.setRes(1);
        }
        return this;
    }

    public DebuggerInit setHost(String host){
        fileLogOkhttpSet.setHOST(host);
        fileCrashOkhttpSet.setHOST(host);
        crashOkhttpSet.setHOST(host);
        logOkhttpSet.setHOST(host);
        return this;
    }

    public DebuggerInit setLogMessageLog(String url){
        fileInit.setUrlLogMessage(url);
        return this;
    }

    public DebuggerInit setFileLogUrl(String url){
        fileInit.setUrlLog(url);
        return this;
    }

    public DebuggerInit setFileCrashUrl(String url){
        fileInit.setUrlCrash(url);
        return this;
    }

    public DebuggerInit setFileClearDay(float day){
        fileInit.setRemoveFileDay(day);
        return this;
    }

    public float getFileClearDay(){
        return fileInit.getRemoveFileDay();
    }

    public CrashSet getCrashSet() {
        return crashSet;
    }

    public DebuggerInit setCrashSet(CrashSet crashSet) {
        this.crashSet = crashSet;
        return this;
    }

    public OkhttpSet getFileCrashOkhttpSet() {
        return fileCrashOkhttpSet;
    }

    public DebuggerInit setFileCrashOkhttpSet(OkhttpSet fileCrashOkhttpSet) {
        this.fileCrashOkhttpSet = fileCrashOkhttpSet;
        return this;
    }

    public OkhttpSet getFileLogOkhttpSet() {
        return fileLogOkhttpSet;
    }

    public DebuggerInit setFileLogOkhttpSet(OkhttpSet fileLogOkhttpSet) {
        this.fileLogOkhttpSet = fileLogOkhttpSet;
        return this;
    }

    public OkhttpSet getCrashOkhttpSet() {
        return crashOkhttpSet;
    }

    public DebuggerInit setCrashOkhttpSet(OkhttpSet crashOkhttpSet) {
        this.crashOkhttpSet = crashOkhttpSet;
        return this;
    }

    public OkhttpSet getLogOkhttpSet() {
        return logOkhttpSet;
    }

    public DebuggerInit setLogOkhttpSet(OkhttpSet logOkhttpSet) {
        this.logOkhttpSet = logOkhttpSet;
        return this;
    }

    public OkhttpConnect getOkhttpFileLogConnect() {
        return okhttpFileLogConnect;
    }

    public OkhttpConnect getOkhttpCrashConnect() {
        return okhttpCrashConnect;
    }

    public OkhttpConnect getOkhttpFileCrashConnect() {
        return okhttpFileCrashConnect;
    }

    public OkhttpConnect getOkhttpLogConnect() {
        return okhttpLogConnect;
    }



}
