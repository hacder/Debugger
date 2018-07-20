package com.ude.debugger.service.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ude.debugger.R;
import com.ude.debugger.utils.WindowUtils;

/**
 * Created by ude on 2017-10-12.
 */

public class PointWindow extends BaseWindow{
    private ImageView img_point_window;
    private static PointWindow pointWindow;//单例
    private FunctionWindow functionWindow;//功能选项窗口


    private PointWindow(Context context){
        super(context);
        initWindow();
        functionWindow = FunctionWindow.getInstance(context,0);
        functionWindow.setOnCloseWindow(new FunctionWindow.OnHintWindow() {
            @Override
            public void onHint() {
                setWindowVisiable(true);
            }
        });
    }

    public static PointWindow getrInstance(Context context){
        if (pointWindow == null){
            pointWindow = new PointWindow(context);
        }
        return pointWindow;
    }

    @Override
    public void createView(){
        super.createView();
        layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        layoutParams.width = WindowUtils.getInstance(windowManager).getWindowWidth()/7;
        layoutParams.height = WindowUtils.getInstance(windowManager).getWindowWidth()/7;
        LayoutInflater inflater = LayoutInflater.from(context);
        ll_window = (LinearLayout) inflater.inflate(R.layout.window_point,null);
        img_point_window = (ImageView)ll_window.findViewById(R.id.img_point_window);
        img_point_window.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return myWindowMove(view,motionEvent);
            }
        });
    }

    @Override
    public void onWindowTouch(View view, MotionEvent motionEvent) {
        setWindowVisiable(false);
        functionWindow.show((int) motionEvent.getRawY());
        super.onWindowTouch(view, motionEvent);
    }

    @Override
    public void onDestroy() {
        if (functionWindow != null){
            functionWindow.onDestroy();
        }
        super.onDestroy();
    }
}
