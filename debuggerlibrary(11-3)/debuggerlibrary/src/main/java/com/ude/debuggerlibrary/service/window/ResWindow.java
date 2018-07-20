package com.ude.debuggerlibrary.service.window;

import android.content.Context;
import android.graphics.Color;
import android.os.Debug;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ude.debuggerlibrary.R;
import com.ude.debuggerlibrary.utils.FunctionSwitch;
import com.ude.debuggerlibrary.utils.ThreadUtils;
import com.ude.debuggerlibrary.utils.WindowUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Created by ude on 2017-10-31.
 */

public class ResWindow extends BaseWindow {
    private static ResWindow resWindow;//单例
    private BufferedReader reader;//adb读取流
    private Process process, processMemory;
    private int pid;//本进程ip
    private LinearLayout ll_move;
    private LineChart line_cpu, line_memory;
    private ArrayList<Entry> valuesCpu = new ArrayList<Entry>();//cpu统计表数据
    private ArrayList<Entry> valuesMemory = new ArrayList<Entry>();//memory统计表数据
    private ArrayList<Entry> valuesRSS = new ArrayList<>();//RSS实际内存值
    private LineDataSet set1, set2, set3;
    private ExecutorService executorService;
    private Debug.MemoryInfo memoryInfo1;
    private boolean isBoxChangeType = false;
    private int num = 0;//计数


    protected ResWindow(Context context) {
        super(context);
        initWindow();
        setWindowVisiable(false);
        pid = android.os.Process.myPid();
    }

    public static ResWindow getInstance(Context context) {
        if (resWindow == null) {
            resWindow = new ResWindow(context);
        }
        return resWindow;
    }

    @Override
    public void createView() {
        super.createView();
        executorService = ThreadUtils.newCachedThreadPool();
        memoryInfo1 = new Debug.MemoryInfo();
        layoutParams.width = WindowUtils.getInstance(windowManager).getWindowWidth();
        layoutParams.height = WindowUtils.getInstance(windowManager).getWindowHeight() / 3;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.START;
        LayoutInflater inflater = LayoutInflater.from(context);
        ll_window = (LinearLayout) inflater.inflate(R.layout.window_res, null);
        ll_move = (LinearLayout) ll_window.findViewById(R.id.ll_move);
        ll_move.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = motionEvent.getX();
                        downY = motionEvent.getY();
                        if (view.getId() == ll_move.getId()) {
                            isBoxChangeType = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isBoxChangeType) {
                            if (isMove || Math.abs(motionEvent.getX() - downX) > 50) {
                                layoutParams.width += (motionEvent.getX() - downX);
                                //除以3为消除抖动
                                if (ll_window != null && layoutParams.width > 100
                                        && layoutParams.width < WindowUtils.getInstance(windowManager).getWindowWidth()) {
                                    windowManager.updateViewLayout(ll_window, layoutParams);
                                }
                                isMove = true;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isBoxChangeType = false;
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
        });
        line_cpu = (LineChart) ll_window.findViewById(R.id.line_cpu);
        line_memory = (LineChart) ll_window.findViewById(R.id.line_memory);
        valuesCpu.add(new Entry(0, 0f));//初始化添加一个(x,y)数据
        valuesMemory.add(new Entry(0, 0f));//初始化添加一个(x,y)数据
        valuesRSS.add(new Entry(0, 0f));//初始化添加一个(x,y)数据
        // 显示的提示文字
        set1 = new LineDataSet(valuesCpu, "cpu占用(%)");
        set2 = new LineDataSet(valuesMemory, "PSS 内存占用(M)");
        set2.setValueFormatter(new DefaultValueFormatter(2));//设置显示的坐标为2位小数
        set3 = new LineDataSet(valuesRSS, "RSS 内存占用(M)");
        set3.setValueFormatter(new DefaultValueFormatter(2));//设置显示的坐标为2位小数

        set1 = initLineChart(line_cpu, set1, "CPU占用", Color.BLACK);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets
        //创建一个线性数据的列表
        LineData data = new LineData(dataSets);
        // set data
        line_cpu.setData(data);

        set2 = initLineChart(line_memory, set2, "内存占用", Color.BLACK);
        set3 = initLineChart(line_memory, set3, "内存占用", Color.BLUE);
        ArrayList<ILineDataSet> dataSets2 = new ArrayList<ILineDataSet>();
        dataSets2.add(set2); // add the datasets
        dataSets2.add(set3);
        //创建一个线性数据的列表
        LineData data2 = new LineData(dataSets2);
        // set data
        line_memory.setData(data2);
        getRes();

    }

    /**
     * 图像初始化
     *
     * @param lineChart
     * @param set
     * @param tip
     */
    public LineDataSet initLineChart(LineChart lineChart, LineDataSet set, String tip, int color) {

//        line_cpu.setOnChartGestureListener(this);
//        line_cpu.setOnChartValueSelectedListener(this);
        lineChart.setDrawGridBackground(false);

        // no description text
        Description description = new Description();
        description.setText(tip);
        lineChart.setDescription(description);
        lineChart.setNoDataText("初始化中");

        lineChart.setTouchEnabled(true);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        // 线性表的属性
//        set.enableDashedLine(10f, 5f, 0f);
//        set.enableDashedHighlightLine(10f, 5f, 0f);
        set.setColor(color);
        set.setCircleColor(color);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(true);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);
        //填充颜色
        set.setFillColor(Color.RED);
        return set;
    }


    /**
     * 资源监控线程
     */
    public void getRes() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    process = Runtime.getRuntime().exec("top -s cpu");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while (true) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        return;
                    }
                    while (FunctionSwitch.Res != 2) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    String readStr;
                    try {
                        if ((readStr = reader.readLine()) != null) {
                            while (isMove) {
                                Thread.sleep(100);
                            }
                            if (readStr.trim().startsWith(pid + "")) {
                                num++;
                                Debug.getMemoryInfo(memoryInfo1);
//                                LogUtil.d("程序的heap的值为:"+Debug.getNativeHeapAllocatedSize()/1024f/1024f);
                                float rate = Float.parseFloat(readStr.substring(
                                        readStr.lastIndexOf(" ", readStr.indexOf("%")) + 1, readStr.indexOf("%")));
                                float memory = Float.parseFloat(
                                        readStr.substring(readStr.indexOf("K", readStr.indexOf("%")) + 1
                                                , readStr.indexOf("K", readStr.indexOf("K", readStr.indexOf("%")) + 1)).trim());
                                valuesMemory.add(new Entry(num, memoryInfo1.getTotalPss() / 1024f));
                                valuesCpu.add(new Entry(num, rate));
                                valuesRSS.add(new Entry(num, memory / 1024f));
                                while (valuesMemory.size() > 80) {
                                    valuesMemory.remove(0);
                                }
                                while (valuesRSS.size() > 80) {
                                    valuesRSS.remove(0);
                                }
                                while (valuesCpu.size() > 80) {
                                    valuesCpu.remove(0);
                                }
                                set1.notifyDataSetChanged();
                                set2.notifyDataSetChanged();
                                set3.notifyDataSetChanged();
                                line_cpu.setVisibleXRangeMaximum(15);
                                line_cpu.getData().notifyDataChanged();
                                line_cpu.moveViewToX(num - 15);
                                line_cpu.notifyDataSetChanged();
                                line_memory.setVisibleXRangeMaximum(10);
                                line_memory.getData().notifyDataChanged();
                                line_memory.moveViewToX(num - 10);
                                line_memory.notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    /**
     * 设置相反的显隐
     */
    public void show() {
        if (ll_window.getVisibility() == View.VISIBLE) {
            ll_window.setVisibility(View.GONE);
        } else {
            ll_window.setVisibility(View.VISIBLE);
        }
    }

}
