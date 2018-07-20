package com.ude.debugger.service.window;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ude.debugger.R;
import com.ude.debugger.adpter.InfoShowAdapter;
import com.ude.debugger.utils.FileInit;
import com.ude.debugger.utils.LogUtil;
import com.ude.debugger.utils.ThreadUtils;
import com.ude.debugger.utils.WindowUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by ude on 2017-10-13.
 */

public class LogcatWindow extends BaseWindow implements View.OnClickListener{
    public static final int V = 0;
    public static final int D = 1;
    public static final int I = 2;
    public static final int W = 3;
    public static final int E = 4;

    private static LogcatWindow logcatWindow;//单例
    private Spinner spinner_lv;
    private RecyclerView recycler_logcat;
    private ImageView img_move,img_clear,img_down;
    private InfoShowAdapter adapter;//log显示适配器
    private ArrayAdapter<String> arrayAdapter;//下拉选项适配器
    private List<String> infoList;//log信息列表
    private List<String> showList;//展示的信息列表
    private List<String> data_list;//下拉选项列表
    private java.lang.Process process;
    private BufferedReader reader;//adb读取流
    private BufferedReader  errorReader;//adb错误读取流
    private ExecutorService executorService;//log线程池
    private int pid;//本进程ip
    private boolean isMoveToBottom = true;//是否自动移动到末尾
    private EditText et_search;
    private int showLevel = V;//筛选显示等级
    private String key = "";//筛选关键字

    private LogcatWindow(Context context) {
        super(context);
        initWindow();
        setWindowVisiable(false);
        pid = android.os.Process.myPid();
        Logcat();
    }

    public static LogcatWindow getInstance(Context context){
        if (logcatWindow == null){
            logcatWindow = new LogcatWindow(context);
        }
        return logcatWindow;
    }

    @Override
    public void createView() {
        super.createView();
        infoList = new ArrayList<>();
        showList = new ArrayList<>();
        data_list = new ArrayList<>();
        layoutParams.width = WindowUtils.getInstance(windowManager).getWindowWidth();
        layoutParams.height = WindowUtils.getInstance(windowManager).getWindowHeight()/3;
//        layoutParams.gravity = Gravity.BOTTOM;
        LayoutInflater inflater = LayoutInflater.from(context);
        ll_window = (LinearLayout) inflater.inflate(R.layout.window_logcat,null);
        img_move = (ImageView) ll_window.findViewById(R.id.img_move);
        img_move.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                myWindowMove(view, motionEvent);
                return false;
            }
        });
        img_clear = (ImageView)ll_window.findViewById(R.id.img_clear);
        img_clear.setOnClickListener(this);
        img_down = (ImageView)ll_window.findViewById(R.id.img_down);
        img_down.setOnClickListener(this);
        spinner_lv = (Spinner)ll_window.findViewById(R.id.spinner_lv);
        spinner_lv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                LogUtil.d("focus:"+b );
                if (ll_window != null && layoutParams != null) {
                    if (b) {
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    } else {
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    }
                    windowManager.updateViewLayout(ll_window, layoutParams);
                }
            }
        });
        et_search = (EditText)ll_window.findViewById(R.id.et_search);
        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                LogUtil.d("et_focus:"+b );
                if (ll_window != null && layoutParams != null) {
                    if (b) {
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    } else {
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    }
                    windowManager.updateViewLayout(ll_window, layoutParams);
                }
            }
        });
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    //隐藏软键盘
                    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputMethodManager.isActive()){
                        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                    }
                    et_search.setFocusable(false);
                    et_search.setFocusableInTouchMode(false);
                    List<String> info = new ArrayList<String>(infoList);
                    List<String> cache = new ArrayList<String>();
                    for (String readStr : info){
                        int level = V;
                        if (readStr.contains(" V ")) {//白,默认
                            level = V;
                        } else if (readStr.contains(" I ")) {//蓝
                            level = I;
                        } else if (readStr.contains(" D ")) {//绿
                            level = D;
                        } else if (readStr.contains(" W ")) {//橙
                            level = W;
                        } else if (readStr.contains(" E ")) {//红
                            level = E;
                        }
                        if (level >= showLevel && readStr.contains(key)){
                            cache.add(readStr);
                        }
                    }
                    showList.clear();
                    showList.addAll(cache);
                    adapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });
        et_search.setFocusable(false);
        et_search.setFocusableInTouchMode(false);
        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_search.isFocusable()) {
                    et_search.setFocusable(true);
                    et_search.setFocusableInTouchMode(true);
                }
            }
        });

        data_list.add("V");
        data_list.add("D");
        data_list.add("I");
        data_list.add("W");
        data_list.add("E");
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data_list);
        spinner_lv.setAdapter(arrayAdapter);
        spinner_lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showLevel = i;
                List<String> info = new ArrayList<String>(infoList);
                List<String> cache = new ArrayList<String>();
                for (String readStr : info){
                    int level = V;
                    if (readStr.contains(" V ")) {//白,默认
                        level = V;
                    } else if (readStr.contains(" I ")) {//蓝
                        level = I;
                    } else if (readStr.contains(" D ")) {//绿
                        level = D;
                    } else if (readStr.contains(" W ")) {//橙
                        level = W;
                    } else if (readStr.contains(" E ")) {//红
                        level = E;
                    }
                    if (level >= showLevel && readStr.contains(key)){
                        cache.add(readStr);
                    }
                }
                showList.clear();
                showList.addAll(cache);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        recycler_logcat = (RecyclerView)ll_window.findViewById(R.id.recycler_logcat);
        recycler_logcat.setItemAnimator(new DefaultItemAnimator());
        recycler_logcat.setLayoutManager(new LinearLayoutManager(context));
        adapter = new InfoShowAdapter(context,showList);
        recycler_logcat.setAdapter(adapter);
    }

    /**
     * 设置相反的显隐
     */
    public void show(){
        if (ll_window.getVisibility() == View.VISIBLE){
            ll_window.setVisibility(View.GONE);
        }else {
            ll_window.setVisibility(View.VISIBLE);
        }
    }

    public void Logcat(){
        try {
            Process process1 = Runtime.getRuntime().exec("logcat -c");//清理之前的log缓存
            process = Runtime.getRuntime().exec("logcat "+context.getPackageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        executorService = ThreadUtils.newSingleThreadPool();
        //log信息流
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        return;
                    }
                    final String readStr;
                    try {
                        if ((readStr = reader.readLine()) != null){
                            if (readStr.contains(" "+pid+" ")) {
                                int level = V;
                                if (readStr.contains(" V ")) {//白,默认
                                    level = V;
                                } else if (readStr.contains(" I ")) {//蓝
                                    level = I;
                                } else if (readStr.contains(" D ")) {//绿
                                    level = D;
                                } else if (readStr.contains(" W ")) {//橙
                                    level = W;
                                } else if (readStr.contains(" E ")) {//红
                                    level = E;
                                }
                                FileInit.getInstance(context).writerContentToLogFile(readStr+"\n");
                                infoList.add(readStr);
                                final int finalLevel = level;
                                recycler_logcat.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalLevel >= showLevel && readStr.contains(key)){
                                            showList.add(readStr);
                                            adapter.notifyDataSetChanged();
                                            if (isMoveToBottom) {
                                                if (showList.size() -1 > 0) {
                                                    recycler_logcat.smoothScrollToPosition(showList.size() - 1);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_clear:
                showList.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.img_down:
                if (isMoveToBottom){
                    img_down.setBackgroundColor(Color.TRANSPARENT);
                    isMoveToBottom = false;
                }else {
                    img_down.setBackgroundColor(Color.WHITE);
                    if (showList.size() -1 > 0) {
                        recycler_logcat.smoothScrollToPosition(showList.size() - 1);
                    }
                    isMoveToBottom = true;
                }
                break;
        }
    }
}
