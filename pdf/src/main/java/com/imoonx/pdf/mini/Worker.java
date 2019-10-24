package com.imoonx.pdf.mini;

import android.app.Activity;

import com.imoonx.util.XLog;

import java.util.concurrent.LinkedBlockingQueue;

public class Worker implements Runnable {

    public static class Task implements Runnable {
        public void work() {
        }

        public void run() {
        }
    }

    protected Activity activity;
    private LinkedBlockingQueue<Task> queue;
    private boolean alive;

    public Worker(Activity act) {
        activity = act;
        queue = new LinkedBlockingQueue<Task>();
    }

    public void start() {
        alive = true;
        new Thread(this).start();
    }

    public void stop() {
        alive = false;
    }

    public void add(Task task) {
        try {
            queue.put(task);
        } catch (InterruptedException x) {
            XLog.e(Worker.class, x.getMessage());
        }
    }

    public void run() {
        while (alive) {
            try {
                Task task = queue.take();
                task.work();
                activity.runOnUiThread(task);
            } catch (Throwable x) {
                XLog.e(Worker.class, x.getMessage());
            }
        }
    }
}
