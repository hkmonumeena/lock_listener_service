package com.example.locklistener.lock_services.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.locklistener.lock_services.config.KeepAliveConfig;
import com.example.locklistener.lock_services.config.NotificationUtils;
import com.example.locklistener.lock_services.receivers.NotificationClickReceiver;

import static com.example.locklistener.lock_services.config.KeepAliveConfig.SP_NAME;



/**
 * 隐藏前台服务通知
 */
public class HideForegroundService extends Service {
    private Handler handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("pfsdfp", "onStartCommand: " );
        startForeground();
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopForeground(true);
                stopSelf();
            }
        }, 2000);
        return START_NOT_STICKY;
    }


    private void startForeground() {
        if (KeepAliveConfig.foregroundNotification != null) {
            Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
            intent.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
            Notification notification = NotificationUtils.createNotification(this,
                    KeepAliveConfig.TITLE,
                    KeepAliveConfig.CONTENT,
                    KeepAliveConfig.DEF_ICONS,
                    intent
            );
            startForeground(KeepAliveConfig.FOREGROUD_NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        Log.e("pfsdfp", "onDestroy: " );
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
