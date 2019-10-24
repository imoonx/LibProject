package com.imoonx.common.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import com.imoonx.util.TDevice;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;

    @TargetApi(Build.VERSION_CODES.O)
    public NotificationUtils(Context base, String appName) {
        super(base);
        createChannels(appName);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels(String appName) {
        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(TDevice.getPackageName(),
                appName, NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(androidChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getAndroidChannelNotification(String title, String body, int smallIcon) {
        return getAndroidChannelNotification(title, body, body, smallIcon, smallIcon);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getAndroidChannelNotification(String title, String body, String bigBody, int smallIcon, int largeIcon) {
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle().setBigContentTitle(title).bigText(bigBody);
        return new Notification.Builder(getApplicationContext(), TDevice.getPackageName())
                .setStyle(bigTextStyle)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), largeIcon))
                .setAutoCancel(true);
    }
}