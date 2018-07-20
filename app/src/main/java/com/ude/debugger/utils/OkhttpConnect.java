package com.ude.debugger.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by ude on 2017-10-11.
 */

public class OkhttpConnect<T> {
    private OkhttpSet okhttpSet;//http配置信息
    private CallBackAction<T> callBackAction = null;
    private TypeToken<T> typeToken;//接收的数据需要转换成的实体类,放在此处已解决java泛型的类型擦除

    public OkhttpConnect(OkhttpSet okhttpSet,TypeToken<T> typeToken) {
        this.okhttpSet = okhttpSet;
        this.typeToken = typeToken;
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
                    throw new RuntimeException("host is null");
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
                            Gson gson = new Gson();
                            T t = gson.fromJson(response.body().string()
                                    , typeToken.getType());
                            Log.i("---T---", t.toString() + ";" + t.getClass());
                            callBackAction.onSuccess(t);
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
                    throw new RuntimeException("host is null");
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
                    }
                }

                @Override
                public void onResponse(Call call, Response response){
                    try {
                        if (callBackAction != null) {
                            Gson gson = new Gson();
                            T t = gson.fromJson(response.body().string()
                                    , typeToken.getType());
                            Log.i("---T---", t.toString() + ";" + t.getClass());
                            callBackAction.onSuccess(t);
                        }
                    }catch (Exception e){
                        if (null != callBackAction) {
                            callBackAction.onFail(e.toString());
                        }
                    }
                }
            });
        }catch (Exception e){
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
    }


    public interface CallBackAction<V>{
        void onSuccess(V model);
        void onFail(String msg);
    }

    public OkhttpConnect<T> setCallBackAction(CallBackAction<T> callBackAction) {
        this.callBackAction = callBackAction;
        return this;
    }
}
