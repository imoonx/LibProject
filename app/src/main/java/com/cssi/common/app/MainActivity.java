package com.cssi.common.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.util.Util;
import com.cssi.common.app.viewer.LibraryActivity;
import com.cssi.common.base.BaseActivity;
import com.cssi.common.http.builder.GetBuilder;
import com.cssi.common.http.callback.StringCallback;
import com.cssi.common.http.request.RequestCall;
import com.cssi.common.util.GsonUtil;
import com.cssi.common.util.Log;
import com.cssi.common.util.TDevice;

import okhttp3.Call;

public class MainActivity extends BaseActivity {

    @Override
    protected void initWidget() {
        findViewById(R.id.scan).setOnClickListener(this);
        findViewById(R.id.video).setOnClickListener(this);
        findViewById(R.id.pdf).setOnClickListener(this);
        findViewById(R.id.pdf_mini).setOnClickListener(this);

        GetBuilder builder = new GetBuilder();
        builder.url("http://10.1.2.58:8081/appsrv/index/bootPage.action");
        RequestCall build = builder.build();
        build.execute(new StringCallback() {
            @Override
            public void onResponse(String response) {
                Log.e(MainActivity.class, response);
                StartModel startModel = GsonUtil.jsonToObject(response, StartModel.class);
                Log.e(MainActivity.class, "startModel是否为空" + (null == startModel));
                if (startModel.getErrCode() == 0) {
                    if (!TextUtils.isEmpty(startModel.getObject().getPicUrl())) {
                        if (Util.isOnMainThread())
                            Log.e(MainActivity.class, "是否是主线程=" + TDevice.isMainThread());
                        Log.e(MainActivity.class, startModel.getObject().getPicUrl());
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1) {
            }
        });
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan:
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
                break;
            case R.id.video:
                startActivity(new Intent(MainActivity.this, VideoActivity.class));
                break;
            case R.id.pdf:
                startActivity(new Intent(MainActivity.this, LibraryActivity.class));
                break;
            case R.id.pdf_mini:
                startActivity(new Intent(MainActivity.this, com.cssi.common.app.mini.LibraryActivity.class));
                break;
        }
    }
}
