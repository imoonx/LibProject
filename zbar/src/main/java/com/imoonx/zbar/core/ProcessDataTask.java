package com.imoonx.zbar.core;

import android.hardware.Camera;
import android.os.AsyncTask;

import com.imoonx.util.XLog;

public class ProcessDataTask extends AsyncTask<Void, Void, String> {
    private Camera mCamera;
    private byte[] mData;
    private Delegate mDelegate;

    public ProcessDataTask(Camera camera, byte[] data, Delegate delegate) {
        mCamera = camera;
        mData = data;
        mDelegate = delegate;
    }

    public ProcessDataTask perform() {
        execute();
        return this;
    }

    public void cancelTask() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mDelegate = null;
    }

    @Override
    protected String doInBackground(Void... params) {
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;

        byte[] rotatedData = new byte[mData.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedData[x * height + height - y - 1] = mData[x + y * width];
            }
        }
        int tmp = width;
        width = height;
        height = tmp;

        try {
            if (mDelegate == null) {
                return null;
            }
            return mDelegate.processData(rotatedData, width, height, false);
        } catch (Exception e1) {
            XLog.e(ProcessDataTask.class, e1.toString());
            try {
                return mDelegate.processData(rotatedData, width, height, true);
            } catch (Exception e2) {
                XLog.e(ProcessDataTask.class, e2.toString());
                return null;
            }
        }
    }

    public interface Delegate {
        String processData(byte[] data, int width, int height, boolean isRetry);
    }
}