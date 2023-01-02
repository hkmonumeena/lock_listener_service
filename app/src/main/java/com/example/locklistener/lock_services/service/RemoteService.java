package com.example.locklistener.lock_services.service;

import static com.example.locklistener.lock_services.config.RunMode.HIGH_POWER_CONSUMPTION;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.keepalivetest.KeepAliveAidl;
import com.example.locklistener.R;
import com.example.locklistener.lock_services.KeepAliveManager;
import com.example.locklistener.lock_services.config.ForegroundNotification;
import com.example.locklistener.lock_services.config.ForegroundNotificationClickListener;
import com.example.locklistener.lock_services.config.KeepAliveConfig;
import com.example.locklistener.lock_services.config.NotificationUtils;
import com.example.locklistener.lock_services.receivers.NotificationClickReceiver;

@SuppressWarnings(value = {"unchecked", "deprecation"})
public final class RemoteService extends Service {
    private RemoteBinder mBilder;
    private String TAG = "RemoteService";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, " onCreate");
        if (mBilder == null) {
            mBilder = new RemoteBinder();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBilder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            this.bindService(new Intent(RemoteService.this, LocalService.class),
                    connection, Context.BIND_ABOVE_CLIENT);

            shouDefNotify();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return START_STICKY;
    }

    private void shouDefNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(KeepAliveConfig.TITLE) && !TextUtils.isEmpty( KeepAliveConfig.CONTENT)) {
                Intent intent2 = new Intent(getApplicationContext(), NotificationClickReceiver.class);
                intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
                Notification notification = NotificationUtils.createNotification(RemoteService.this, KeepAliveConfig.TITLE, KeepAliveConfig.CONTENT, KeepAliveConfig.DEF_ICONS, intent2);
                startForeground(KeepAliveConfig.FOREGROUD_NOTIFICATION_ID, notification);
                start();
            }
        }
    }
    public void start() {

        KeepAliveManager.toKeepAlive(
                getApplication()
                , HIGH_POWER_CONSUMPTION,
                "Caller",
                "message",
                R.mipmap.ic_launcher,
                new ForegroundNotification(
                        new ForegroundNotificationClickListener() {
                            @Override
                            public void foregroundNotificationClick(Context context, Intent intent) {
                                Log.e(TAG, "foregroundNotificationClick: " );
                            }
                        })
        );
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private final class RemoteBinder extends KeepAliveAidl.Stub {
        @Override
        public void wakeUp(String title, String discription, int iconRes) throws RemoteException {
            Log.i(TAG, "wakeUp");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (title != null || discription != null) {
                    KeepAliveConfig.CONTENT = title;
                    KeepAliveConfig.DEF_ICONS = iconRes;
                    KeepAliveConfig.TITLE = discription;
                }
                if (KeepAliveConfig.TITLE != null && KeepAliveConfig.CONTENT != null) {
                    Intent intent2 = new Intent(getApplicationContext(), NotificationClickReceiver.class);
                    intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
                    Notification notification = NotificationUtils.createNotification(RemoteService.this, KeepAliveConfig.TITLE, KeepAliveConfig.CONTENT, KeepAliveConfig.DEF_ICONS, intent2);
                    startForeground(KeepAliveConfig.FOREGROUD_NOTIFICATION_ID, notification);
                    Log.d("JOB-->", TAG + "2 显示通知栏");
                }
            }
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Intent remoteService = new Intent(RemoteService.this,
                    LocalService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                RemoteService.this.startForegroundService(remoteService);
            } else {
                RemoteService.this.startService(remoteService);
            }
            RemoteService.this.bindService(new Intent(RemoteService.this,
                    LocalService.class), connection, Context.BIND_ABOVE_CLIENT);
            PowerManager pm = (PowerManager) RemoteService.this.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            if (isScreenOn) {
           //     sendBroadcast(new Intent("_ACTION_SCREEN_ON"));
            } else {
                sendBroadcast(new Intent("_ACTION_SCREEN_OFF"));
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            shouDefNotify();
        }
    };

}
