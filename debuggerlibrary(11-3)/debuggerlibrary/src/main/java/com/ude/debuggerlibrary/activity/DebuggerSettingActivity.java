package com.ude.debuggerlibrary.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ude.debuggerlibrary.R;
import com.ude.debuggerlibrary.activity.filelist.FileListActivity;
import com.ude.debuggerlibrary.adpter.SettingAdapter;
import com.ude.debuggerlibrary.adpter.listener.OnItemClickListener;
import com.ude.debuggerlibrary.utils.FunctionSwitch;

import java.util.ArrayList;
import java.util.List;

public class DebuggerSettingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recycler_setting;
    private SettingAdapter adapter;
    private List<String> strings = new ArrayList<>();//设置列表
    private List<Integer> types = new ArrayList<>();//设置列表状态
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debugger_setting);
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        initView();
    }

    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("调试器设置");
        toolbar.setTitleTextColor(Color.WHITE);
        recycler_setting = (RecyclerView) findViewById(R.id.recycler_setting);
        recycler_setting.setLayoutManager(new LinearLayoutManager(this));
        recycler_setting.setItemAnimator(new DefaultItemAnimator());
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        strings.add("调试功能");
        types.add(FunctionSwitch.Debugger);

        strings.add("Log信息显示功能");
        types.add(FunctionSwitch.LogShow);

        strings.add("Log信息自动记录到文件");
        types.add(FunctionSwitch.LogSave);

        strings.add("资源监视功能");
        types.add(FunctionSwitch.Res);

        strings.add("崩溃处理功能");
        types.add(FunctionSwitch.Crash);

        strings.add("崩溃信息自动记录到文件");
        types.add(FunctionSwitch.CrashSave);

        strings.add("查看已记录的文件");
        types.add(SettingAdapter.NOSWICH);
//        strings.add("版本信息");
//        types.add(SettingAdapter.NOSWICH);
        adapter = new SettingAdapter(this, strings, types);
        recycler_setting.setAdapter(adapter);
        if (types.get(0) == SettingAdapter.NOCHOOSE) {
            for (int i = 1; i < types.size(); i++) {
                if (types.get(i) != SettingAdapter.NOSWICH) {
                    types.set(i, SettingAdapter.NOCHOOSE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(strings.get(i), SettingAdapter.NOCHOOSE);
                    editor.apply();
                    FunctionSwitch.setData(strings.get(i), SettingAdapter.NOCHOOSE);
                }
            }
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                if (types.get(position) == SettingAdapter.NOSWICH) {
                    if (strings.get(position).contains("查看已记录的文件")) {
                        Intent intent = new Intent(DebuggerSettingActivity.this, FileListActivity.class);
                        startActivity(intent);
                    }
                } else {
                    if (position == 0) {
                        if (types.get(0) == SettingAdapter.NOCHOOSE) {
                            for (int i = 1; i < types.size(); i++) {
                                if (types.get(i) != SettingAdapter.NOSWICH) {
                                    types.set(i, SettingAdapter.NOCHOOSE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putInt(strings.get(i), SettingAdapter.NOCHOOSE);
                                    editor.apply();
                                    FunctionSwitch.setData(strings.get(i), SettingAdapter.NOCHOOSE);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        if (types.get(0) == SettingAdapter.NOCHOOSE) {
                            types.set(0, SettingAdapter.CHOOSE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt(strings.get(0), SettingAdapter.CHOOSE);
                            editor.apply();
                            FunctionSwitch.setData(strings.get(0), SettingAdapter.CHOOSE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void OnLongClick(View view, int position) {

            }
        });
    }
}
