package com.ude.debugger.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yw on 2017-10-11.
 * 线程池工具类
 * shutDown():关闭线程池,不关闭已提交线程
 * shutDownNoe():关闭线程池,尝试关闭正在执行的
 * allowCoreThreadTimeOut(boolean)是否允许核心进程空闲时被回收,默认不回收
 * execute(R):提交线程R,执行
 * submit(R):提交线程R,执行(有返回值)
 */

public class ThreadUtils {
    /**
     * FixTheadPool线程池
     * 固定线程数量,且全为核心线程,不会回收线程
     * newFixThreadPool().execute(new Runnable)
     * @param num 线程数
     * @return
     */
    public static ExecutorService newFixThreadPool(int num){
        return new ThreadPoolExecutor(num,num,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    /**
     * SingleThreadPool线程池
     * 只有一个线程且为核心线程
     * newSingleThreadPool().execute(new Runnable)
     * @return
     */
    public static ExecutorService newSingleThreadPool(){
        return new ThreadPoolExecutor(1,1,0,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    /**
     * CacheThreadPool线程池
     * 只有非核心线程,60秒空闲的线程会被回收,此处任务会立即执行
     * newCachedThreadPool().execute(new Runnable)
     * @return
     */
    public static ExecutorService newCachedThreadPool(){
        return new ThreadPoolExecutor(0,Integer.MAX_VALUE,60L,TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>());
    }

    /**
     * ScheduledThreadPool线程池
     * 核心线程数固定,非核心在空闲时直接回收,并具有延时执行功能及重复执行功能
     * newScheduledThreadPool(2).scheduleAtFixedRate(new Runnable(),1000,2000,TimeUnit.MILLISECONDS);
     * 延时1秒执行,每个2秒执行一次runnable
     * @param corePoolSize 核心线程数
     * @return
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize){
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }


}
