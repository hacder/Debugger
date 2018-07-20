package com.ude.debugger.service.window;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ude.debugger.R;
import com.ude.debugger.activity.DebuggerSettingActivity;
import com.ude.debugger.adpter.FunctionWindowAdapter;
import com.ude.debugger.adpter.listener.OnItemClickListener;
import com.ude.debugger.utils.WindowUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ude on 2017-10-12.
 */

public class FunctionWindow extends BaseWindow{
    private static FunctionWindow functionWindow = null;//单例
    private RecyclerView recycler_function_window;
    private LinearLayout ll_function;//选择窗口
    private static int startY = 0;//显示的y(创建此窗口时需要传入位置,否则默认为0)
    private FunctionWindowAdapter adapter;//recycler适配器
    private List<String> strings;//选择的文字列表
    private List<Integer> resList;//选择的图片,这里固定使用资源文件
    private OnHintWindow onHintWindow;//关闭窗口的监听器
    private LogcatWindow logcatWindow;//log窗口

    private FunctionWindow(Context context) {
        super(context);
        initWindow();
        setWindowVisiable(false);
        logcatWindow = LogcatWindow.getInstance(context);
    }

    public static FunctionWindow getInstance(Context context,int touchY){
        startY = touchY;
        if (functionWindow == null){
            functionWindow = new FunctionWindow(context);
        }
        return functionWindow;
    }

    @Override
    public void createView() {
        super.createView();
        layoutParams.width = WindowUtils.getInstance(windowManager).getWindowWidth();
        layoutParams.height = WindowUtils.getInstance(windowManager).getWindowHeight();
        LayoutInflater inflater = LayoutInflater.from(context);
        ll_window = (LinearLayout) inflater.inflate(R.layout.window_function,null);
        ll_function = (LinearLayout) ll_window.findViewById(R.id.ll_window);
        //根据点击Y轴的位置来设置窗口出现的位置
        if (startY < WindowUtils.getInstance(windowManager).getWindowHeight()/3){
            ll_function.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
        }else if (startY < WindowUtils.getInstance(windowManager).getWindowHeight()*2/3){
            ll_function.setGravity(Gravity.CENTER);
        }else {
            ll_function.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        }
        ll_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show(0);
            }
        });
        recycler_function_window = (RecyclerView)ll_window.findViewById(R.id.recycler_function_window);
        recycler_function_window.setLayoutManager(new GridLayoutManager(context,2));
        recycler_function_window.setItemAnimator(new DefaultItemAnimator());
        if (strings == null){
            strings = new ArrayList<>();
        }
        if (resList == null){
            resList = new ArrayList<>();
        }
        strings.clear();
        strings.add("Log查看");
        strings.add("序资源使用情况");
        strings.add("设置");
        resList.clear();
        resList.add(R.drawable.ic_assignment_black_24dp);
        resList.add(R.drawable.ic_build_black_24dp);
        resList.add(R.drawable.ic_settings_black_24dp);
        adapter = new FunctionWindowAdapter(context,strings,resList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                switch (strings.get(position)){
                    case "Log查看":
                        //log窗口
                        if (logcatWindow == null) {
                            logcatWindow = LogcatWindow.getInstance(context);
                        }else {
                            logcatWindow.show();
                        }
                        break;
                    case "序资源使用情况":
                        //资源查看窗口
                        break;
                    case "设置":
                        //设置界面
                        Intent intent = new Intent(context, DebuggerSettingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                }
                show(0);
            }

            @Override
            public void OnLongClick(View view, int position) {

            }
        });
        recycler_function_window.setAdapter(adapter);
    }

    /**
     * 设置相反的显隐
     */
   public void show(int touchY){
       if (touchY != 0) {
           startY = touchY;
       }
       if (ll_window != null){
           if (ll_window.getVisibility() == View.VISIBLE){
               ll_window.setVisibility(View.GONE);
               if (onHintWindow != null){
                   onHintWindow.onHint();
               }
           }else {
               ll_window.setVisibility(View.VISIBLE);
               //根据点击Y轴的位置来设置窗口出现的位置
               if (startY < WindowUtils.getInstance(windowManager).getWindowHeight() / 3) {
                   ll_function.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
               } else if (startY < WindowUtils.getInstance(windowManager).getWindowHeight() * 2 / 3) {
                   ll_function.setGravity(Gravity.CENTER);
               } else {
                   ll_function.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
               }
               windowManager.updateViewLayout(ll_window, layoutParams);
           }
       }
   }

    @Override
    public void onDestroy() {
        if (logcatWindow != null){
            logcatWindow.onDestroy();
        }
        super.onDestroy();
    }

    public void setOnCloseWindow(OnHintWindow onHintWindow) {
        this.onHintWindow = onHintWindow;
    }

    /**
     * 窗口关闭监听器
     */
    public interface OnHintWindow{
        void onHint();
    }
}
