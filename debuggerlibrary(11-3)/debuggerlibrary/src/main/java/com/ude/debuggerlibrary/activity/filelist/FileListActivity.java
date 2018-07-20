package com.ude.debuggerlibrary.activity.filelist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ude.debuggerlibrary.R;
import com.ude.debuggerlibrary.activity.filebrowsing.FileBrowsingActivity;
import com.ude.debuggerlibrary.activity.search.SearchActivity;
import com.ude.debuggerlibrary.adpter.CheckAdapter;
import com.ude.debuggerlibrary.adpter.listener.OnItemClickListener;
import com.ude.debuggerlibrary.data.CheckData;
import com.ude.debuggerlibrary.utils.FileInit;
import com.ude.debuggerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity implements FileListConstract {
    private Toolbar toolbar;
    private LinearLayout ll_search;//搜索
    private RecyclerView recycler_file;
    private ImageView img_search;
    private CheckAdapter adapter;
    private List<CheckData> strings = new ArrayList<>();//文件列表
    private List<CheckData> defaultList = new ArrayList<>();//默认文件列表
    private boolean isFirst = true;//是否为第一级的目录
    private FileListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        presenter = new FileListPresenter(this, this);
        initView();
    }

    public void initView() {
        img_search = (ImageView) findViewById(R.id.img_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.isCheckMode()) {
                    final List<CheckData> upDatas = new ArrayList<CheckData>();//未上传的
                    final List<CheckData> datas = new ArrayList<CheckData>();//全部上传的
                    for (CheckData checkData : strings) {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(FileListActivity.this).setTitle("提示")
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
                } else {
                    Intent intent = new Intent(FileListActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
            }
        });
        recycler_file = (RecyclerView) findViewById(R.id.recycler_file);
        recycler_file.setLayoutManager(new LinearLayoutManager(this));
        recycler_file.setItemAnimator(new DefaultItemAnimator());
        defaultList.add(new CheckData("Log文件", "Log文件", true, false, 0));
        defaultList.add(new CheckData("Crash文件", "Crash文件", true, false, 0));
        strings.addAll(defaultList);
        adapter = new CheckAdapter(this, strings);
        recycler_file.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                if (adapter.isCheckMode()) {
                    strings.get(position).setCheck(!strings.get(position).isCheck());
                    adapter.notifyItemChanged(position);
                } else {
                    if (isFirst) {
                        if (strings.get(position).getFileName().contains("Log")) {
                            presenter.openFileDirectory("Log");
                        } else if (strings.get(position).getFileName().contains("Crash")) {
                            presenter.openFileDirectory("Crash");
                        }
                    } else {
                        Intent intent = new Intent(FileListActivity.this, FileBrowsingActivity.class);
                        intent.putExtra("path", strings.get(position).getFileName());
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void OnLongClick(View view, int position) {
                if (!adapter.isCheckMode()) {
                    adapter.setCheckMode(true);
                    adapter.notifyDataSetChanged();
                    img_search.setImageResource(R.drawable.ic_vertical_align_top_black_24dp);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (adapter.isCheckMode()) {
            adapter.setCheckMode(false);
            img_search.setImageResource(R.drawable.ic_search_black_24dp);
            adapter.notifyDataSetChanged();
            return;
        }
        if (!isFirst) {
            toFirst();
            return;
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
    public void toFirst() {
        strings.clear();
        strings.addAll(defaultList);
        adapter.notifyDataSetChanged();
        isFirst = true;
    }

    /**
     * 获取文件列表成功
     *
     * @param fileLists
     */
    @Override
    public void onGetFileSuccess(List<String> fileLists) {
        strings.clear();
        for (String name : fileLists) {
            strings.add(new CheckData(name, name, true, false, 0));
        }
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
                ToastUtils.showSingleToast(msg);
            }
        });

    }

    @Override
    public void onNoFileFound() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showSingleToast("文件夹为空");
            }
        });
    }

    @Override
    public void onSearchSuccess(final CheckData checkData) {
        recycler_file.post(new Runnable() {
            @Override
            public void run() {
                strings.add(checkData);
                adapter.notifyDataSetChanged();
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

}
