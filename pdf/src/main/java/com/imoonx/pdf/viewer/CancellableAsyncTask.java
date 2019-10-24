package com.imoonx.pdf.viewer;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.imoonx.util.XLog;

public class CancellableAsyncTask<Params, Result> {

    private final AsyncTask<Params, Void, Result> asyncTask;
    private final CancellableTaskDefinition<Params, Result> ourTask;

    public void onPreExecute() {
    }

    public void onPostExecute(Result result) {
    }

    @SuppressLint("StaticFieldLeak")
    public CancellableAsyncTask(final CancellableTaskDefinition<Params, Result> task) {
        if (task == null)
            throw new IllegalArgumentException();

        this.ourTask = task;
        asyncTask = new AsyncTask<Params, Void, Result>() {
            @Override
            protected Result doInBackground(Params... params) {
                return task.doInBackground(params);
            }

            @Override
            protected void onPreExecute() {
                CancellableAsyncTask.this.onPreExecute();
            }

            @Override
            protected void onPostExecute(Result result) {
                CancellableAsyncTask.this.onPostExecute(result);
                task.doCleanup();
            }

            @Override
            protected void onCancelled(Result result) {
                task.doCleanup();
            }
        };
    }

    public void cancel() {
        this.asyncTask.cancel(true);
        ourTask.doCancel();
        try {
            this.asyncTask.get();
        } catch (Exception e) {
            XLog.e(CancellableAsyncTask.class, e.toString());
        }
    }

    public void execute(Params... params) {
        asyncTask.execute(params);
    }

}
