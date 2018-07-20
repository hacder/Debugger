package com.ude.debugger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ude.debugger.data.BaseData;
import com.ude.debugger.data.TimeData;
import com.ude.debugger.utils.OkhttpConnect;
import com.ude.debugger.utils.OkhttpSet;
import com.ude.debugger.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private Button bt;
    private OkhttpSet okhttpSet;
    private OkhttpConnect<BaseData<TimeData>> okhttpConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.tv);
        bt = (Button)findViewById(R.id.bt);

        okhttpSet = new OkhttpSet();
        okhttpSet.setHOST("http://api.xizhi.com");
        okhttpConnect = new OkhttpConnect<BaseData<TimeData>>(okhttpSet,new TypeToken<BaseData<TimeData>>(){});

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okhttpConnect.setCallBackAction(new OkhttpConnect.CallBackAction<BaseData<TimeData>>() {
                    @Override
                    public void onSuccess(BaseData<TimeData> model) {
                        Log.i("---success---",model.toString());
                    }

                    @Override
                    public void onFail(String msg) {
                        Log.e("---fail---",msg);
                    }
                }).httpGet("/v1/timestamp");
            }
        });
    }


}
