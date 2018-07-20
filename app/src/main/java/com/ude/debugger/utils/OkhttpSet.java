package com.ude.debugger.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ude on 2017-10-10.
 * 网络连接配置类
 * 可配置域名、请求头、是否失败重连、是否显示Log、连接/读/写超时时间及其单位、文件上传格式、文件上传requestBodyFile,自定义request
 * 使用自定义request时,域名、请求头、连接/读/写超时时间及其单位失效,都使用自定义request内设置的参数
 */

public class OkhttpSet {
    private MediaType mediaTypeFile = MultipartBody.FORM;//文件post上传的格式,默认为FROM
    private MultipartBody.Builder requestBodyFile;//文件上传消息体,在此处添加附加的消息
    private String host ;
    private Request.Builder request = null;//http连接信息设置
    private TimeUnit timeUnit = TimeUnit.SECONDS;//超时时间单位,默认为妙
    private Interceptor interceptorHeader = null;//头部拦截器
    //默认全功能开启
    private boolean isretryOnConnectionFailure = true;//是否失败重连
    private boolean isShowLog = true;//是否显示Log信息
    //超时时间全部默认为20秒
    private int connectTime = 20;//连接超时时间
    private int readTime = 20;//数据读取超时时间
    private int writeTime = 20;//数据写入超时时间

    public OkhttpSet(){}


    public String getHOST() {
        return host;
    }

    public void setHOST(String host) {
        this.host = host;
    }

    public Request.Builder getRequest() {
        return request;
    }

    public void setRequest(Request.Builder request) {
        this.request = request;
    }

    public int getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(int writeTime) {
        this.writeTime = writeTime;
    }

    public boolean isretryOnConnectionFailure() {
        return isretryOnConnectionFailure;
    }

    public void setIsretryOnConnectionFailure(boolean isretryOnConnectionFailure) {
        this.isretryOnConnectionFailure = isretryOnConnectionFailure;
    }

    public boolean isShowLog() {
        return isShowLog;
    }

    public void setShowLog(boolean showLog) {
        isShowLog = showLog;
    }

    public int getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(int connectTime) {
        this.connectTime = connectTime;
    }

    public int getReadTime() {
        return readTime;
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Interceptor getInterceptorHeader() {
        return interceptorHeader;
    }

    public void setInterceptorHeader(Interceptor interceptorHeader) {
        this.interceptorHeader = interceptorHeader;
    }

    public MediaType getMediaTypeFile() {
        return mediaTypeFile;
    }

    public void setMediaTypeFile(MediaType mediaTypeFile) {
        this.mediaTypeFile = mediaTypeFile;
    }

    public MultipartBody.Builder getRequestBodyFile() {
        return requestBodyFile;
    }

    public void setRequestBodyFile(MultipartBody.Builder requestBodyFile) {
        this.requestBodyFile = requestBodyFile;
    }
}
