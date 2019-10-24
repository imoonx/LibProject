package com.cssi.common.app;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cssi.common.util.Log;
import com.cssi.common.zbar.ZBarView;
import com.cssi.common.zbar.core.QRCodeView;
import com.cssi.common.zbar.core.ScanResultCallback;

/**
 * 扫描
 */
public class ScanActivity extends AppCompatActivity implements ScanResultCallback, View.OnTouchListener {

    private TextView mCarSign;
    private TextView mOrderNo;
    private View mViewRoot;
    private Button mBind;
    private boolean isLightOpen = false;
    private Vibrator vibrator;

    private QRCodeView mQRCodeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initView();
    }

    private void initView() {
        mViewRoot = findViewById(R.id.root_view);
        mQRCodeView = (ZBarView) findViewById(R.id.zbarview);
        mQRCodeView.setScanResultCallback(this);
        mQRCodeView.setOnTouchListener(this);
        mCarSign = (TextView) findViewById(R.id.car_sign);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        if (null != vibrator)
            vibrator.cancel();
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    //振动器
    private void vibrate() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{100, 400, 100, 400}, -1);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //扫描成功
    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.e(ScanActivity.class, "result:" + result);
        vibrate();
        mCarSign.setText(result);
        mQRCodeView.stopSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(ScanActivity.class, "打开相机出错");
    }

    //触屏监听 打开关闭闪光灯
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isLightOpen) {
            mQRCodeView.openFlashlight();
        } else {
            mQRCodeView.closeFlashlight();
        }
        isLightOpen = !isLightOpen;
        return false;
    }
}
