package com.imoonx.common.update;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.imoonx.common.R;
import com.imoonx.util.Res;
import com.imoonx.util.TDevice;
import com.imoonx.util.Toast;
import com.imoonx.util.XIOUtil;
import com.imoonx.util.XLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * 下载服务
 */
public class DownloadService extends Service {

    public static final String BUNDLE_KEY_DOWNLOAD_URL = "download_url";
    public static final String BUNDLE_KEY_TITLE = "title";
    private ICallbackResult callback;
    private static final int NOTIFY_ID = 0;
    private int progress;
    private NotificationManager mNotificationManager;
    private boolean canceled;
    private String downloadUrl;
    private String mTitle = "正在下载%s";
    private String saveFileName;
    private DownloadBinder binder;
    private boolean serviceIsDestroy = false;
    private Context mContext = this;
    private Thread downLoadThread;
    private Notification mNotification;
    // private static final int RC_READ_PHONE_PERM = 0x01;
    private static final int DOWN_FINISH = 0;// 下载完成
    private static final int DOWN_UPDATING = 1;// 下载更新中
    private static final int CANCEL_NOTIFICATION = 2;// 取消通知
    private static final int SERVICE_EXCEPTION = 3;// 时间超时
    private static final int OTHER_EXCEPTION = 4;

    /**
     * 下载路径
     *
     * @return 下载路径
     */
    protected String getDownPath() {
        return Environment.getExternalStorageDirectory() + File.separator + "CSSI" + File.separator + "down" + File.separator;
    }

    @Override
    public void onDestroy() {
        mNotificationManager.cancel(NOTIFY_ID);
        super.onDestroy();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWN_FINISH:
                    // 下载完毕
                    XLog.i(DownloadService.class, "下载完成");
                    mNotificationManager.cancel(NOTIFY_ID);
                    installApk();
                    stopSelf();// 停掉服务自身
                    break;
                case CANCEL_NOTIFICATION:
                    // 取消通知
                    mNotificationManager.cancel(NOTIFY_ID);
                    stopSelf();// 停掉服务自身
                    break;
                case SERVICE_EXCEPTION:
                    mNotificationManager.cancel(NOTIFY_ID);
                    Toast.showToast("下载服务异常,请稍后重试!");
                    stopSelf();// 停掉服务自身
                    break;
                case OTHER_EXCEPTION:
                    mNotificationManager.cancel(NOTIFY_ID);
                    Toast.showToast("apk更新失败,请稍后重试!");
                    stopSelf();// 停掉服务自身
                    break;
                case DOWN_UPDATING:
                    int rate = msg.arg1;
                    XLog.i(DownloadService.class, "下载数据" + rate);
                    if (rate < 100) {
                        RemoteViews contentview = mNotification.contentView;
                        contentview.setTextViewText(R.id.tv_download_state, mTitle + "(" + rate + "%" + ")");
                        contentview.setProgressBar(R.id.pb_download, 100, rate, true);
                    } else {
                        // 下载完毕后变换通知形式
                        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                        mNotification.contentView = null;
                        serviceIsDestroy = true;
                        stopSelf();// 停掉服务自身
                    }
                    mNotificationManager.notify(NOTIFY_ID, mNotification);
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // 下载地址
        downloadUrl = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_URL);
        XLog.e(DownloadService.class, "下载地址：" + downloadUrl);
        // 保存路径
        saveFileName = getDownPath() + getSaveFileName(downloadUrl);
        mTitle = String.format(mTitle, intent.getStringExtra(BUNDLE_KEY_TITLE));
        return binder;
    }

    // 获取文件名
    private String getSaveFileName(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return "";
        }
        return downloadUrl.substring(downloadUrl.lastIndexOf("/"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new DownloadBinder();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        stopForeground(true);// 这个不确定是否有作用
    }

    private void startDownload() {
        XLog.i(DownloadService.class, "应用下载");
        canceled = false;
        downloadApk();
    }

    /**
     * 创建通知
     */
    @SuppressWarnings("deprecation")
    private void setUpNotification() {
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification_show);
        contentView.setTextViewText(R.id.tv_download_state, mTitle);
        if (Build.VERSION.SDK_INT < 26) {
            int icon = R.drawable.notification_logo;
            CharSequence tickerText = Res.getString(R.string.ready_down);
            mNotification = new Notification(icon, tickerText, System.currentTimeMillis());
            mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            // 指定个性化视图
            mNotification.contentView = contentView;
            mNotificationManager.notify(NOTIFY_ID, mNotification);
        } else {
            String channelID = TDevice.getPackageName();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelID, "123", importance);
            mNotificationManager.createNotificationChannel(mChannel);
            Notification.Builder builder = new Notification.Builder(mContext, channelID)
                    .setSmallIcon(R.drawable.notification_logo)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setAutoCancel(false);
            mNotification = builder.build();
            mNotification.contentView = contentView;
            mNotificationManager.notify(NOTIFY_ID, mNotification);
        }
    }

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            XLog.e(DownloadService.class, "文件不存在");
            return;
        }
        installAPK(apkfile);
    }

    /**
     * 自定义安装
     * 适配Android N O
     * 添加Intent.FLAG_ACTIVITY_NEW_TASK
     *
     * @param apkfile 安装文件
     */
    protected void installAPK(File apkfile) {
        if (!apkfile.exists())
            return;
        XLog.i(DownloadService.class, "安装应用");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), TDevice.getPackageName() + ".xuan.provider", apkfile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        public void run() {
            File file = new File(getDownPath());
            if (!file.exists()) {
                file.mkdirs();
            }
            String apkFile = saveFileName;
            File saveFile = new File(apkFile);
            try {
                if (!TextUtils.isEmpty(downloadUrl)) {
                    downloadUpdateFile(downloadUrl, saveFile);
                } else {
                    mHandler.sendEmptyMessage(OTHER_EXCEPTION);
                }
            } catch (Exception e) {
                XLog.i(DownloadService.class, e);
                mHandler.sendEmptyMessage(OTHER_EXCEPTION);
            }
        }
    };

    // 联网下载
    public long downloadUpdateFile(String downloadUrl, File saveFile) {
        int downloadCount = 0;
        long totalSize = 0;
        int updateTotalSize;

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");

            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
//            fos = this.openFileOutput(saveFile.getName(), FLAG_GRANT_WRITE_URI_PERMISSION);
            byte buffer[] = new byte[1024];
            int readsize;
            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalSize += readsize;
                // 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 4 > downloadCount) {
                    downloadCount += 4;
                    // 更新进度
                    Message msg = mHandler.obtainMessage();
                    msg.what = DOWN_UPDATING;
                    msg.arg1 = downloadCount;
                    mHandler.sendMessage(msg);
                    if (callback != null)
                        callback.OnBackResult(progress);
                }
            }
            //修改读写权限
            try {
                String command = "chmod 777 " + saveFile.getAbsolutePath();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(command);
            } catch (IOException e) {
                XLog.e(DownloadService.class, e);
            }
            // 下载完成通知安装
            mHandler.sendEmptyMessage(DOWN_FINISH);
            // 下载完了，cancelled也要设置
            canceled = true;
        } catch (SocketTimeoutException e) {
            XLog.e(DownloadService.class, "SocketTimeoutException:" + e);
            mHandler.sendEmptyMessage(SERVICE_EXCEPTION);
        } catch (MalformedURLException e) {
            XLog.e(DownloadService.class, "MalformedURLException:" + e);
            mHandler.sendEmptyMessage(OTHER_EXCEPTION);
        } catch (IOException e) {
            XLog.e(DownloadService.class, "IOException:" + e);
            mHandler.sendEmptyMessage(OTHER_EXCEPTION);
        } catch (Exception e) {
            XLog.e(DownloadService.class, "Exception:" + e);
            mHandler.sendEmptyMessage(SERVICE_EXCEPTION);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            XIOUtil.close(is, fos);
        }
        return totalSize;
    }

    public class DownloadBinder extends Binder {
        public void start() {
            if (downLoadThread == null || !downLoadThread.isAlive()) {
                progress = 0;
                setUpNotification();
                new Thread() {
                    @Override
                    public void run() {
                        // 下载
                        startDownload();
                    }
                }.start();
            }
        }

        public void cancel() {
            canceled = true;
        }

        public int getProgress() {
            return progress;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public boolean serviceIsDestroy() {
            return serviceIsDestroy;
        }

        public void cancelNotification() {
            mHandler.sendEmptyMessage(CANCEL_NOTIFICATION);
        }

        public void addCallback(ICallbackResult callback) {
            DownloadService.this.callback = callback;
        }
    }
}
