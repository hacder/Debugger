package com.ude.debugger.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ude.debugger.R;
import com.ude.debugger.activity.filelist.FileListActivity;
import com.ude.debugger.adpter.SettingAdapter;
import com.ude.debugger.adpter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        }catch (NullPointerException e){}
        initView();
    }

    public void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("调试器设置");
        toolbar.setTitleTextColor(Color.WHITE);
        recycler_setting = (RecyclerView)findViewById(R.id.recycler_setting);
        recycler_setting.setLayoutManager(new LinearLayoutManager(this));
        recycler_setting.setItemAnimator(new DefaultItemAnimator());
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        int type;
        strings.add("调试功能");
        if ((type = sp.getInt("调试功能",0)) != 0){
            types.add(type);
        }else {
            types.add(SettingAdapter.CHOOSE);
        }
        strings.add("Log信息显示功能");
        if ((type = sp.getInt("Log信息显示功能",0)) != 0){
            types.add(type);
        }else {
            types.add(SettingAdapter.CHOOSE);
        }
        strings.add("Log信息自动记录到文件");
        if ((type = sp.getInt("Log信息自动记录到文件",0)) != 0){
            types.add(type);
        }else {
            types.add(SettingAdapter.CHOOSE);
        }
        strings.add("崩溃处理功能");
        if ((type = sp.getInt("崩溃处理功能",0)) != 0){
            types.add(type);
        }else {
            types.add(SettingAdapter.CHOOSE);
        }
        strings.add("崩溃信息自动记录到文件");
        if ((type = sp.getInt("崩溃信息自动记录到文件",0)) != 0){
            types.add(type);
        }else {
            types.add(SettingAdapter.CHOOSE);
        }
        strings.add("查看已记录的文件");
        types.add(SettingAdapter.NOSWICH);
        strings.add("版本信息");
        types.add(SettingAdapter.NOSWICH);
        adapter = new SettingAdapter(this,strings,types);
        recycler_setting.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                if (types.get(position) == SettingAdapter.NOSWICH){
                    if (strings.get(position).contains("查看已记录的文件")){
                        Intent intent = new Intent(DebuggerSettingActivity.this, FileListActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void OnLongClick(View view, int position) {

            }
        });
    }
}
