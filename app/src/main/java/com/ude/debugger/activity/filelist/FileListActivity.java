package com.ude.debugger.activity.filelist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ude.debugger.R;
import com.ude.debugger.activity.filebrowsing.FileBrowsingActivity;
import com.ude.debugger.adpter.SettingAdapter;
import com.ude.debugger.adpter.listener.OnItemClickListener;
import com.ude.debugger.utils.FileInit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity implements FileListConstract{
    private Toolbar toolbar;
    private LinearLayout ll_search;//搜索
    private RecyclerView recycler_file;
    private SettingAdapter adapter;
    private List<String> strings = new ArrayList<>();//文件列表
    private List<String> defaultList = new ArrayList<>();//默认文件列表
    private boolean isFirst = true;//是否为第一级的目录
    private FileListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        try {
            getSupportActionBar().hide();
        }catch (NullPointerException e){}
        presenter = new FileListPresenter(this,this);
        initView();
    }

    public void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        ll_search = (LinearLayout)findViewById(R.id.ll_search);
        recycler_file = (RecyclerView)findViewById(R.id.recycler_file);
        recycler_file.setLayoutManager(new LinearLayoutManager(this));
        recycler_file.setItemAnimator(new DefaultItemAnimator());
        defaultList.add("Log文件");
        defaultList.add("Crash文件");
        strings.addAll(defaultList);
        adapter = new SettingAdapter(this,strings,null);
        recycler_file.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                if (isFirst){
                    if (strings.get(position).contains("Log")){
                        presenter.openFileDirectory("Log");
                    }else if (strings.get(position).contains("Crash")){
                        presenter.openFileDirectory("Crash");
                    }
                }else {
                    Intent intent = new Intent(FileListActivity.this, FileBrowsingActivity.class);
                    intent.putExtra("path",strings.get(position));
                    startActivity(intent);
                }
            }

            @Override
            public void OnLongClick(View view, int position) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!isFirst){
            toFirst();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    /**
     * 跳转至文件首页
     */
    public void toFirst(){
        strings.clear();
        strings.addAll(defaultList);
        adapter.notifyDataSetChanged();
        isFirst = true;
    }

    /**
     * 获取文件列表成功
     * @param fileLists
     */
    @Override
    public void onGetFileSuccess(List<String> fileLists) {
        strings.clear();
        strings.addAll(fileLists);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                isFirst = false;
            }
        });
    }

    /**
     * 获取文件列表失败
     */
    @Override
    public void onGetFileFail(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileListActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onNoFileFound() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileListActivity.this,"文件夹为空",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
