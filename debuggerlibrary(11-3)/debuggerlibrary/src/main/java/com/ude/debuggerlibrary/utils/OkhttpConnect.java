package com.ude.debuggerlibrary.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by ude on 2017-10-11.
 */

public class OkhttpConnect {
    private OkhttpSet okhttpSet;//http配置信息
    private CallBackAction callBackAction = null;
    private Context context;
    private String fileNameCache = "";//文件名缓存

    public OkhttpConnect(OkhttpSet okhttpSet,Context context) {
        this.okhttpSet = okhttpSet;
        this.context = context;
    }

    /**
     * get请求
     * @param url 接口
     */
    public void httpGet(String url){
        try {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            if (okhttpSet.isShowLog()){//添加Log拦截器
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();//打印出请求的信息的拦截器
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//设置打印请求的记录级别  NONE:不记录; BASIC:请求/响应行; HEADER:请求/响应行+请求头; BODY:请求/响应行+请求头+请求体(所有信息)
                httpClient.addInterceptor(interceptor);
            }
            if (okhttpSet.getInterceptorHeader() != null){//添加头部拦截器
                httpClient.addInterceptor(okhttpSet.getInterceptorHeader());
            }
            httpClient.retryOnConnectionFailure(okhttpSet.isretryOnConnectionFailure())//设置是否失败重连
                    .connectTimeout(okhttpSet.getConnectTime(),okhttpSet.getTimeUnit())//连接超时间
                    .readTimeout(okhttpSet.getReadTime(),okhttpSet.getTimeUnit())//读超时
                    .writeTimeout(okhttpSet.getWriteTime(),okhttpSet.getTimeUnit());//写超时
            Call call;
            if (okhttpSet.getRequest() == null) {//不使用自定义
                if (okhttpSet.getHOST() == null){
                    return;
                }
                call = httpClient.build().newCall(new Request.Builder().url(okhttpSet.getHOST() + url).build());
            }else {//使用自定义request
                call = httpClient.build().newCall(okhttpSet.getRequest().build());
            }
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (callBackAction != null) {
                        callBackAction.onFail(e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response){
                    try {
                        if (callBackAction != null) {
                            callBackAction.onSuccess(response.body().string());
                        }
                    }catch (Exception e){
                        if (null != callBackAction) {
                            callBackAction.onFail(e.toString());
                        }
                    }
                }
            });
        }catch (Exception e){
            if (null != callBackAction) {
                callBackAction.onFail(e.getMessage());
            }
        }
    }
    /**
     * post请求
     * @param url 接口
     * @param requestBody 消息体
     */
    public void httpPost(String url, RequestBody requestBody) {
        httpPost(url,requestBody,false,null);
    }

    /**
     * post请求
     * @param url 接口
     * @param requestBody 消息体
     * @param isFile 是否为文件上传
     */
    public void httpPost(String url, RequestBody requestBody, final boolean isFile, final CallBackAction myCallBackAction) {
        try {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            if (okhttpSet.isShowLog()){//添加Log拦截器
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();//打印出请求的信息的拦截器
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//设置打印请求的记录级别  NONE:不记录; BASIC:请求/响应行; HEADER:请求/响应行+请求头; BODY:请求/响应行+请求头+请求体(所有信息)
                httpClient.addInterceptor(interceptor);
            }
            if (okhttpSet.getInterceptorHeader() != null){//添加头部拦截器
                httpClient.addInterceptor(okhttpSet.getInterceptorHeader());
            }
            httpClient.retryOnConnectionFailure(okhttpSet.isretryOnConnectionFailure())//设置是否失败重连
                    .connectTimeout(okhttpSet.getConnectTime(),okhttpSet.getTimeUnit())//连接超时间
                    .readTimeout(okhttpSet.getReadTime(),okhttpSet.getTimeUnit())//读超时
                    .writeTimeout(okhttpSet.getWriteTime(),okhttpSet.getTimeUnit());//写超时
            Call call;
            if (okhttpSet.getRequest() == null) {//不使用自定义
                if (okhttpSet.getHOST() == null || url == null){
                    return;
                }
                call = httpClient.build().newCall(new Request.Builder().url(okhttpSet.getHOST() + url).post(requestBody).build());
            }else {//使用自定义request
                call = httpClient.build().newCall(okhttpSet.getRequest().post(requestBody).build());
            }
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (callBackAction != null) {
                        callBackAction.onFail(e.getMessage());
                        if (isFile){
                            Message message = new Message();
                            message.what = 1;
                            message.obj = "上传文件失败";
                            handler.sendMessage(message);
                        }
                    }
                    if (myCallBackAction != null){
                        myCallBackAction.onFail(e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response){
                    try {
                        if (isFile){
                            Message message = new Message();
                            message.what = 1;
                            message.obj = "上传文件成功";
                            handler.sendMessage(message);
                            FileInit.getInstance(context).addUploadFlag(fileNameCache);
                        }
                        if (callBackAction != null) {
                            callBackAction.onSuccess(response.body().string());
                        }
                        if (myCallBackAction != null){
                            myCallBackAction.onSuccess(response.body().string());
                        }
                    }catch (Exception e){
                        if (null != callBackAction) {
                            callBackAction.onFail(e.toString());
                        }
                        if (isFile){
                            Message message = new Message();
                            message.what = 1;
                            message.obj = "上传文件失败";
                            handler.sendMessage(message);
                        }
                        if (myCallBackAction != null){
                            myCallBackAction.onFail(e.toString());
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            if (isFile){
                Message message = new Message();
                message.what = 1;
                message.obj = "上传文件失败";
                handler.sendMessage(message);
            }
            if (callBackAction != null) {
                callBackAction.onFail(e.getMessage());
            }
        }
    }



    /**
     * 上传文件
     * @param file
     * @param url
     */
    public void upFile(File file,String url){
        fileNameCache = file.getName();
        RequestBody requestBody;
        if (okhttpSet.getRequestBodyFile() == null){
            requestBody = new MultipartBody.Builder()
                    .setType(okhttpSet.getMediaTypeFile())
                    .addFormDataPart("file",file.getName(),RequestBody.create(MultipartBody.FORM, file))
                    .build();
        }else {
            requestBody = okhttpSet.getRequestBodyFile()
                    .setType(okhttpSet.getMediaTypeFile())
                    .addFormDataPart("file",file.getName(),RequestBody.create(MultipartBody.FORM, file))
                    .build();
        }
        httpPost(url,requestBody);
        FileInit.getInstance(context).addUploadFlag(file.getName());
    }


    public interface CallBackAction{
        void onSuccess(Object model);
        void onFail(String msg);
    }

    public OkhttpConnect setCallBackAction(CallBackAction callBackAction) {
        this.callBackAction = callBackAction;
        return this;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    ToastUtils.showSingleToast(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
