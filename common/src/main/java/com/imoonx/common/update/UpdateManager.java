package com.imoonx.common.update;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.imoonx.common.ui.dialog.DialogHelper;
import com.imoonx.common.ui.dialog.WaitDialog;
import com.imoonx.http.callback.StringCallback;
import com.imoonx.util.XLog;

import okhttp3.Call;

/**
 * 更新管理类
 */
public abstract class UpdateManager {

    protected Context mContext;
    protected boolean isShow;
    private WaitDialog _waitDialog;
    private ServiceConnection conn;

    public ServiceConnection getConn() {
        return conn;
    }

    public void setConn(ServiceConnection conn) {
        this.conn = conn;
    }

    public UpdateManager(Context context, boolean isShow) {
        this.mContext = context;
        this.isShow = isShow;
    }

    public void checkUpdate() {
        if (isShow) {
            showCheckDialog();
        }
        requestDate();
    }

    /**
     * 下载请求
     */
    protected abstract void requestDate();

    protected StringCallback mCallback = new StringCallback() {
        @Override
        public void onResponse(String respose) {
            XLog.i(UpdateManager.class, "更新数据：" + respose);
            hideCheckDialog();
            onParse(respose);
        }

        @Override
        public void onError(Call arg0, Exception e) {
            XLog.e(UpdateManager.class, "更新数据：" + e.toString());
            hideCheckDialog();
            if (isShow) {
                showFaileDialog();
            }
        }
    };

    /**
     * 显示检查提醒
     */
    protected void showCheckDialog() {
        Activity activity = checkActivity();
        if (activity == null) return;
        if (_waitDialog == null) {
            _waitDialog = DialogHelper.getWaitDialog((Activity) mContext, "正在获取新版本信息...");
        }
        _waitDialog.show();
    }

    protected abstract void onParse(String respose);

    protected void hideCheckDialog() {
        if (_waitDialog != null) {
            _waitDialog.dismiss();
        }
    }

    /**
     * 显示更新
     */
    protected abstract void showUpdateInfo();

    /**
     * 无更新提醒
     */
    protected void showLatestDialog() {
        Activity activity = checkActivity();
        if (activity == null) return;
        DialogHelper.getMessageDialog(activity, "已经是新版本了").show();
    }

    private Activity checkActivity() {
        Activity activity = (Activity) mContext;
        if (null == activity || activity.isFinishing())
            return null;
        return activity;
    }

    /**
     * 失败Dialog
     */
    protected void showFaileDialog() {
        Activity activity = checkActivity();
        if (activity == null) return;
        DialogHelper.getMessageDialog(activity, "网络异常，无法获取版本信息").show();
    }

    /**
     * 下载服务
     *
     * @param context 上下文
     * @param downurl 下载地址
     * @param tilte   提醒标题
     */
    protected void openDownLoadService(Context context, String downurl, String tilte) {
        final ICallbackResult callback = new ICallbackResult() {
            @Override
            public void OnBackResult(Object s) {
            }
        };

        conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
                binder.addCallback(callback);
                binder.start();
            }
        };
        Intent intent = new Intent(context, getDownloadService());
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, downurl);
        intent.putExtra(DownloadService.BUNDLE_KEY_TITLE, tilte);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    protected Class<DownloadService> getDownloadService() {
        return DownloadService.class;
    }

}
