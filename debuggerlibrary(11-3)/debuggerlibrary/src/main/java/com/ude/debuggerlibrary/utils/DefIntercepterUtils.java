package com.ude.debuggerlibrary.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ude on 2017-11-03.
 */

public class DefIntercepterUtils {
    private Interceptor mInterceptor;

    public DefIntercepterUtils(final String app, final String appKey){
        //默认头部拦截器
        mInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
                String head = original.url().toString().toLowerCase()
                        + appKey
                        + time
                        + System.currentTimeMillis() / 1000;
                //这里添加头部,这里可以用addHeader来添加多个头部,如果使用header方法就只能添加一个头部
                Request.Builder requestBuilder = original.newBuilder()//添加头部信息
                        .addHeader("app", app)
                        .addHeader("st", System.currentTimeMillis() / 1000+"")
                        .addHeader("device", "1")
                        .addHeader("utoken", MD5.md5(head));
                Request request = requestBuilder.build();//用设置好的requestBuilder建立一个新的request
                return chain.proceed(request);
            }
        };
    }

    public Interceptor getmInterceptor(){
        return mInterceptor;
    }



}
