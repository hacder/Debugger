package com.ude.debugger.activity.filebrowsing;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ude.debugger.R;
import com.ude.debugger.adpter.InfoShowAdapter;
import com.ude.debugger.utils.FileInit;
import com.ude.debugger.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBrowsingActivity extends AppCompatActivity implements FileBrowsingConstract,View.OnClickListener{
    private RecyclerView recycler_browsing;
    private Toolbar toolbar;
    private TextView tv_toolbar,tv_fail;
    private FrameLayout fl_fail;
    private LinearLayout ll_more,ll_search_box,ll_search;
    private ImageView img_search;
    private SearchView search;
    private Button bt_search;
    private String filePath;//文件链接
    private File file;//打开的文件
    private InfoShowAdapter adapter;
    private List<String> infoList = new ArrayList<>();//信息列表
    private List<String> showList = new ArrayList<>();//信息显示列表
    private FileBrowsingPresenter presenter;
    private boolean isSearch = false;//是否为搜索模式
    private String searchKey = "";//搜索关键字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browsing);
        presenter = new FileBrowsingPresenter(this,this);
        filePath = getIntent().getStringExtra("path");
        if (filePath.contains("LOG")){
            filePath = FileInit.getInstance(this).getFILE_LOG()+filePath;
        }else if (filePath.contains("CRASH")){
            filePath = FileInit.getInstance(this).getFILE_CRASH()+filePath;
        }
        file = new File(filePath);
        try {
            getSupportActionBar().hide();
        }catch (NullPointerException e){}
        initView();
        presenter.getFileContent(file);
    }

    public void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        tv_toolbar = (TextView)findViewById(R.id.tv_toolbar);
        tv_toolbar.setText(file.getName());
        tv_fail = (TextView)findViewById(R.id.tv_fail);
        search = (SearchView) findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchKey != null && searchKey.trim().length()>0){
                    showList.clear();
                    adapter.notifyDataSetChanged();
                    presenter.getListContentByKry(infoList,searchKey);
                }else {
                    Toast.makeText(FileBrowsingActivity.this,"请输入搜索关键字",Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchKey = newText;
                return false;
            }
        });
        bt_search = (Button)findViewById(R.id.bt_search);
        bt_search.setOnClickListener(this);
        ll_more = (LinearLayout)findViewById(R.id.ll_more);
        ll_search_box = (LinearLayout)findViewById(R.id.ll_search_box);
        ll_search = (LinearLayout)findViewById(R.id.ll_search);
        img_search = (ImageView)findViewById(R.id.img_search);
        img_search.setOnClickListener(this);
        fl_fail = (FrameLayout)findViewById(R.id.fl_fail);
        fl_fail.setOnClickListener(this);
        recycler_browsing = (RecyclerView)findViewById(R.id.recycler_browsing);
        recycler_browsing.setLayoutManager(new LinearLayoutManager(this));
        recycler_browsing.setItemAnimator(new DefaultItemAnimator());
        adapter = new InfoShowAdapter(this,showList);
        recycler_browsing.setAdapter(adapter);
    }


    @Override
    public void onGetOneFileContent(String content) {
        infoList.add(content);
        showList.add(content);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fl_fail.getVisibility() == View.VISIBLE){
                    fl_fail.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onGetFileFail(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fl_fail.setVisibility(View.VISIBLE);
                tv_fail.setText(msg);
            }
        });
    }

    @Override
    public void onSearchSuccess(String content) {
        showList.add(content);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_fail:
                if (!isSearch) {
                    presenter.getFileContent(file);
                }
                break;
            case R.id.img_search:
                if (presenter.isLoading()){
                    Toast.makeText(this,"文件加载中,请稍后...",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (isSearch){
                    ll_search_box.setVisibility(View.GONE);
                    img_search.setImageResource(R.drawable.ic_search_black_24dp);
                    isSearch = false;
                    if (infoList.size() != showList.size()){
                        showList.clear();
                        showList.addAll(infoList);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    ll_search_box.setVisibility(View.VISIBLE);
                    img_search.setImageResource(R.drawable.ic_cached_black_24dp);
                    isSearch = true;
                }
                break;
            case R.id.bt_search:
                if (searchKey !=null && searchKey.trim().length()>0){
                    showList.clear();
                    adapter.notifyDataSetChanged();
                    presenter.getListContentByKry(infoList,searchKey);
                }else {
                    Toast.makeText(this,"请输入搜索关键字",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
