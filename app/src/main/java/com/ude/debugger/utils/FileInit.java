package com.ude.debugger.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * Created by ude on 2017-10-10.
 * 文件功能初始化类,可配置参数:
 * log目录、ceash目录、urlLog上传log文件接口、urlCrash上传crash文件接口、removeFileday文件保存天数
 */

public class FileInit {
    private String FILE_LOG;//Log目录
    private String FILE_CRASH;//crash目录
    private static FileInit fileInit;//单例
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss_");//时间转换格式,用于定义文件名
    private String urlLog = null;//log文件上传的接口
    private String urlCrash = null;//crash文件上传接口
    private String fileNameLog;//操作的LOG文件名
    private String fileNameCrash;//操作的Crash文件名
    private File fileLog,fileCrash;//操作的文件
    private Context context;
    private float removeFileDay = 5;//删除几天前的文件,默认5天
    private boolean isInit = false;//是否初始化完毕
    private ExecutorService executorService;//自动回收线程池
    private ExecutorService executorServiceLog,executorServiceCrash;//单线程运行的线程池
    private SharedPreferences sp;//首选项标记上传次数

    /**
     * 获取单例
     * @param context
     * @return
     */
    public static FileInit getInstance(Context context){
        if (fileInit == null){
            fileInit = new FileInit(context);
        }
        return fileInit;
    }

    private FileInit(Context context){
        this.context = context;
        FILE_LOG = Environment.getExternalStorageDirectory().getPath()+"/data/"+context.getPackageName()+"/Debugger/Log/";//Log目录
        FILE_CRASH = Environment.getExternalStorageDirectory().getPath()+"/data/"+context.getPackageName()+"/Debugger/Crash/";//crash目录
        fileNameLog = "LOG_"+simpleDateFormat.format(new Date())+System.currentTimeMillis()+".log";
        fileNameCrash = "CRASH_"+simpleDateFormat.format(new Date())+System.currentTimeMillis()+".crash";
        fileLog = new File(FILE_LOG+fileNameLog);
        fileCrash = new File(FILE_CRASH+fileNameCrash);
        executorService = ThreadUtils.newCachedThreadPool();
        executorServiceLog = ThreadUtils.newSingleThreadPool();
        executorServiceCrash = ThreadUtils.newSingleThreadPool();
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        if (unExistsDHandler(FILE_LOG,fileNameLog) && unExistsDHandler(FILE_CRASH,fileNameCrash)){
            isInit = true;
        }
        LogUtil.d("isInit:"+isInit);
        fileClear(FILE_LOG);
        fileClear(FILE_CRASH);

    }

    /**
     * 写入Log数据
     * @param content
     * @throws IOException
     */
    public void writerContentToLogFile(final String content){
        if (!isPermission()) {
            return;
        }
        executorServiceLog.execute(new Runnable() {
            @Override
            public void run() {
                writerFile(fileLog, content);
            }
        });
    }

    /**
     * 写入Log数据
     * @param content
     * @throws IOException
     */
    public void writerContentToCrashFile(final String content){
        if (!isPermission()) {
            return;
        }
        executorServiceCrash.execute(new Runnable() {
            @Override
            public void run() {
                writerFile(fileCrash, content);
            }
        });
    }


    /**
     * 写入数据
     * @param file
     * @param content
     */
    public void writerFile(File file, String content) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            bufferedWriter.write(content);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清理一定时间之前的文件
     */
    public void fileClear(final String filePath) {
        if (isPermission()) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    File file = new File(filePath);
                    if (file.exists()) {
                        File[] files = file.listFiles();
                        for (File fileL : files) {
                            String name = fileL.getName();
                            if (name.startsWith("LOG_") || name.startsWith("CRASH_")) {
                                long time = fileL.lastModified();
                                if (System.currentTimeMillis() - time > removeFileDay * 86400000) {
                                    removeFile(fileL);
                                }
                            }
                        }
                    }
                }

            });
        }
    }

    /**
     * 文件上传次数标价加1
     * @param filename
     */
    public void addUploadFlag(String filename){
        int time = sp.getInt(filename,0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(filename,++time);
        editor.apply();
    }

    /**
     * 删除文件上传次数标记
     * @param filename
     */
    public void removeUploadFlage(String filename){
        if (sp.getInt(filename,999999) != 999999) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(filename);
            editor.apply();
        }
    }

    /**
     * 获取文件上传次数标记
     * @param filename
     * @return
     */
    public int getUploadFlag(String filename){
        return sp.getInt(filename,0);
    }

    /**
     * 删除文件
     * @param file
     */
    public boolean removeFile(final File file) {
        if (isPermission() && file != null) {
            if (!file.getName().equals(fileNameLog) && !file.getName().equals(fileNameCrash)) {//是否为使用中的文件
                if (file.exists()) {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.d("removeFile:"+file.getName());
                            file.delete();
                            removeUploadFlage(file.getName());
                        }
                    });
                }
            } else {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     *
     * 判断文件是否存在,不存在时创建文件
     * 若文件名为空,则只判断文件夹是否存在
     * @param filePath
     * @param fileName
     * @return
     */
    public boolean unExistsDHandler(String filePath,String fileName){
        if (!isPermission()){
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()){
            if (!file.mkdirs()){
                LogUtil.d(filePath+"mkdir fail");
                return false;
            }
        }
        if (fileName != null){
            file = new File(filePath+fileName);
            LogUtil.d("CreateFile:"+fileName);
            if (!file.exists()){
                try {
                    if (!file.createNewFile()){
                        LogUtil.d(fileName+"createfile fail");
                        return false;
                    }
                } catch (IOException e) {
                    return  false;
                }
            }
        }
        return true;
    }



    /**
     * 检测是否有文件读写权限
     * @return
     */
    public boolean isPermission(){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            try {
                Toast.makeText(context,"无文件读写权限,文件读写功能失效",Toast.LENGTH_SHORT).show();
            }catch (RuntimeException e){
            }
            return false;
        }
        return true;
    }

    /**
     * 关闭资源
     */
    public void onDestroy(){
        executorService.shutdownNow();
        executorServiceCrash.shutdownNow();
        executorServiceLog.shutdownNow();
    }

    public String getFILE_LOG() {
        return FILE_LOG;
    }

    public void setFILE_LOG(String FILE_LOG) {
        this.FILE_LOG = FILE_LOG;
    }

    public String getFILE_CRASH() {
        return FILE_CRASH;
    }

    public void setFILE_CRASH(String FILE_CRASH) {
        this.FILE_CRASH = FILE_CRASH;
    }

    public String getUrlLog() {
        return urlLog;
    }

    public void setUrlLog(String urlLog) {
        this.urlLog = urlLog;
    }

    public String getUrlCrash() {
        return urlCrash;
    }

    public void setUrlCrash(String urlCrash) {
        this.urlCrash = urlCrash;
    }

    public float getRemoveFileDay() {
        return removeFileDay;
    }

    public void setRemoveFileDay(float removeFileDay) {
        this.removeFileDay = removeFileDay;
    }

    public boolean isInit() {
        return isInit;
    }

    public File getFileCrash() {
        return fileCrash;
    }

    public void setFileCrash(File fileCrash) {
        this.fileCrash = fileCrash;
    }

    public File getFileLog() {
        return fileLog;
    }

    public void setFileLog(File fileLog) {
        this.fileLog = fileLog;
    }

    public String getFileNameLog() {
        return fileNameLog;
    }

    public String getFileNameCrash() {
        return fileNameCrash;
    }
}
