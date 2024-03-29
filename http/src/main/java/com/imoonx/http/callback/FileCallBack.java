package com.imoonx.http.callback;

import com.imoonx.http.OkHttpUtil;
import com.imoonx.util.XIOUtil;
import com.imoonx.util.XLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * 下载文件回调
 */
public abstract class FileCallBack extends Callback<File> {
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;

    public abstract void progress(float progress);

    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    @Override
    public File parseResponse(Response response) throws Exception {
        return saveFile(response);
    }

    public File saveFile(Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            XLog.e(FileCallBack.class, "File Size=" + total);
            long sum = 0;
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
//            String filePath = destFileDir + destFileName;
//            XLog.e(FileCallBack.class, "filePath=" + filePath);
//            File file = new File(filePath);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                OkHttpUtil.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        progress(finalSum * 1.0f / total);
                    }
                });
            }
            XIOUtil.flush(fos);
            return file;
        } finally {
            XIOUtil.close(is, fos);
        }
    }

}
