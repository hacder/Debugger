package com.ude.debuggerlibrary.service.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by ude on 2017-10-12.
 */

public class BaseWindow {
    protected Context context;
    protected WindowManager.LayoutParams layoutParams;//小窗口布局配置器
    protected WindowManager windowManager;//窗口管理器
    protected float downX = 0;
    protected float downY = 0;
    protected boolean isMove = false;//是否已经移动了,是的话不用经过最小移动距离的筛选
    protected LinearLayout ll_window;

    protected BaseWindow(Context context) {
        this.context = context;
    }

    /**
     * 初始化窗口
     */
    public void initWindow() {
        createView();
        addWindow();
    }

    /**
     * 设置窗口的显示与隐藏法
     *
     * @param isVisiable
     */
    public void setWindowVisiable(boolean isVisiable) {
        if (ll_window != null) {
            if (isVisiable) {
                ll_window.setVisibility(View.VISIBLE);
            } else {
                ll_window.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 创建视图
     */
    public void createView() {
        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) context.getApplicationContext()
                .getSystemService(context.WINDOW_SERVICE);
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    /**
     * 添加视图
     */
    public void addWindow() {
        windowManager.addView(ll_window, layoutParams);
        ll_window.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    /**
     * 移除视图
     */
    public void removeSelfWindow() {
        if (ll_window != null) {
            windowManager.removeViewImmediate(ll_window);
            ll_window = null;
        }
    }

    /**
     * 小窗口移动操作
     *
     * @param view
     * @param motionEvent
     * @return
     */
    public boolean myWindowMove(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = motionEvent.getX();
                downY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMove || Math.abs(motionEvent.getX() - downX) > 50 || Math.abs(motionEvent.getY() - downY) > 50) {
                    layoutParams.x += (motionEvent.getX() - downX) / 3;
                    layoutParams.y += (motionEvent.getY() - downY) / 3;
                    //除以3为消除抖动
                    if (ll_window != null) {
                        windowManager.updateViewLayout(ll_window, layoutParams);
                    }
                    isMove = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    isMove = false;
                    return true;
                } else {
                    onWindowTouch(view, motionEvent);
                }
                break;
        }
        return false;
    }

    /**
     * 窗口触控事件
     *
     * @param view
     * @param motionEvent
     */
    public void onWindowTouch(View view, MotionEvent motionEvent) {
    }

    /**
     * 释放资源
     */
    public void onDestroy() {
        removeSelfWindow();
    }
}
