package com.ude.debuggerlibrary.activity.search;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ude.debuggerlibrary.R;
import com.ude.debuggerlibrary.activity.filebrowsing.FileBrowsingActivity;
import com.ude.debuggerlibrary.adpter.CheckAdapter;
import com.ude.debuggerlibrary.adpter.InfoShowAdapter;
import com.ude.debuggerlibrary.adpter.MyArrayAdapter;
import com.ude.debuggerlibrary.adpter.listener.OnItemClickListener;
import com.ude.debuggerlibrary.data.CheckData;
import com.ude.debuggerlibrary.utils.DebuggerInit;
import com.ude.debuggerlibrary.utils.FileInit;
import com.ude.debuggerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchConstract {
    private Toolbar toolbar;
    private Spinner spinner1, spinner2, spinner3;
    private SearchView searchView;
    private LinearLayout ll_up;
    private TextView tv_toobar;
    private List<String> list1 = new ArrayList<>(), list2 = new ArrayList<>(), list3 = new ArrayList<>();//筛选条件列表
    private List<CheckData> result = new ArrayList<>();//搜索结果
    private String key = "";//关键字
    private String where = "全部";//筛选位置
    private int time = 0;//筛选时间
    private int level = InfoShowAdapter.V;//筛选等级
    private MyArrayAdapter adapter1, adapter2, adapter3;//下拉筛选适配器
    private RecyclerView recycler_search;
    private CheckAdapter adapter;//搜索结果适配器
    private SearchPresenter presenter;
    private FrameLayout fl_tip;
    private TextView tv_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        presenter = new SearchPresenter(this);
        initView();
    }

    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //筛选位置
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        adapter1 = new MyArrayAdapter(this, android.R.layout.simple_spinner_item);
        adapter1.add("全部");
        adapter1.add("文件名");
        adapter1.add("文件内容");
        adapter1.add("位置");
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner1.setAdapter(adapter1);
        spinner1.setSelection(adapter1.getCount());
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != adapterView.getCount()) {
                    where = adapter1.getItem(i);
                }
                if (i == 1) {
                    spinner3.setVisibility(View.GONE);
                } else {
                    spinner3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //筛选时间
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        adapter2 = new MyArrayAdapter(this, android.R.layout.simple_spinner_item);
        adapter2.add("不限");
        for (int i = 1; i <= (int) DebuggerInit.getInstance(getApplication()).getFileClearDay(); i++) {
            adapter2.add(i + "天内");
        }
        adapter2.add("时间");
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(adapter2.getCount());
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != adapterView.getCount()) {
                    time = i + 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //筛选等级
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        adapter3 = new MyArrayAdapter(this, android.R.layout.simple_spinner_item);
        adapter3.add("V");
        adapter3.add("D");
        adapter3.add("I");
        adapter3.add("W");
        adapter3.add("E");
        adapter3.add("等级");
        spinner3.setAdapter(adapter3);
        spinner3.setSelection(adapter3.getCount());
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != adapterView.getCount()) {
                    level = i;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.cancelTask();
                result.clear();
                adapter.notifyDataSetChanged();
                key = query;
                if (fl_tip.getVisibility() == View.VISIBLE) {
                    fl_tip.setVisibility(View.GONE);
                }
                switch (where) {
                    case "全部":
                        presenter.searchKeyByFileName(key, time);
                        presenter.searchKeyByFileContent(key, time, level);
                        break;
                    case "位置":
                        presenter.searchKeyByFileName(key, time);
                        presenter.searchKeyByFileContent(key, time, level);
                        break;
                    case "文件名":
                        presenter.searchKeyByFileName(key, time);
                        break;
                    case "文件内容":
                        presenter.searchKeyByFileContent(key, time, level);
                        break;

                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        tv_toobar = (TextView) findViewById(R.id.tv_toolbar);
        recycler_search = (RecyclerView) findViewById(R.id.recycler_search);
        recycler_search.setLayoutManager(new LinearLayoutManager(this));
        recycler_search.setItemAnimator(new DefaultItemAnimator());
        adapter = new CheckAdapter(this, result);
        recycler_search.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                if (adapter.isCheckMode()) {
                    result.get(position).setCheck(!result.get(position).isCheck());
                    adapter.notifyItemChanged(position);
                } else {
                    Intent intent = new Intent(SearchActivity.this, FileBrowsingActivity.class);
                    intent.putExtra("path", result.get(position).getFileName());
                    intent.putExtra("where", result.get(position).getWhere());
                    startActivity(intent);
                }
            }

            @Override
            public void OnLongClick(View view, int position) {
                if (!adapter.isCheckMode()) {
                    adapter.setCheckMode(true);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        fl_tip = (FrameLayout) findViewById(R.id.fl_tip);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        ll_up = (LinearLayout) findViewById(R.id.ll_up);
        ll_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.cancelTask();
                final List<CheckData> upDatas = new ArrayList<CheckData>();//未上传的
                final List<CheckData> datas = new ArrayList<CheckData>();//全部上传的
                for (CheckData checkData : result) {
                    if (checkData.isCheck()) {
                        if (FileInit.getInstance(getApplicationContext()).getUploadFlag(checkData.getFileName()) == 0) {//未上传过的
                            upDatas.add(checkData);
                        }
                        datas.add(checkData);
                    }
                }
                if (datas.size() == 0) {
                    ToastUtils.showSingleToast("请选择文件或信息");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this).setTitle("提示")
                            .setMessage("本次上传文件共" + datas.size()
                                    + "个,其中已上传过的有" + (datas.size() - upDatas.size()) + "个,是否继续上传?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton("全部上传", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //上传选中的全部文件
                                    presenter.upMessage(datas);
                                    dialogInterface.dismiss();
                                }
                            });
                    if (datas.size() - upDatas.size() > 0) {
                        builder.setNeutralButton("仅上传未上传的文件与信息", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //上传未上传的文件与信息
                                presenter.upMessage(upDatas);
                                dialogInterface.dismiss();
                            }
                        });
                    }
                    builder.show();
                }
            }
        });
    }

    @Override
    public void onSearchSuccess(final CheckData checkData) {
        recycler_search.post(new Runnable() {
            @Override
            public void run() {
                fl_tip.setVisibility(View.GONE);
                result.add(checkData);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (adapter.isCheckMode()) {
            adapter.setCheckMode(false);
            adapter.notifyDataSetChanged();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSearchFail(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showSingleToast(msg);
            }
        });
    }

    @Override
    public void onSearchFinish(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result.size() == 0) {
                    fl_tip.setVisibility(View.VISIBLE);
                    tv_tip.setText("未找到对应的" + msg);
                }
            }
        });

    }

    @Override
    public void onUpMessageFinish(final int success, final int fails) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showSingleToast("上传完成,其中" + success + "个成功," + fails + "个失败");
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
