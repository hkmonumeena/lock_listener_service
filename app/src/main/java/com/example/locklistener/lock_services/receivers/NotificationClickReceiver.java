package com.example.locklistener.lock_services.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.locklistener.lock_services.config.KeepAliveConfig;


public final class NotificationClickReceiver extends BroadcastReceiver {
    public final static String CLICK_NOTIFICATION = "CLICK_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NotificationClickReceiver.CLICK_NOTIFICATION)) {
            if (KeepAliveConfig.foregroundNotification != null) {
                if (KeepAliveConfig.foregroundNotification.getForegroundNotificationClickListener() != null) {
                    KeepAliveConfig.foregroundNotification.getForegroundNotificationClickListener().foregroundNotificationClick(context, intent);
                }
            }
        }
    }
}
