package com.example.locklistener.lock_services;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import android.util.Log;

import com.example.locklistener.lock_services.config.ForegroundNotification;
import com.example.locklistener.lock_services.config.KeepAliveConfig;
import com.example.locklistener.lock_services.config.NotificationUtils;
import com.example.locklistener.lock_services.config.RunMode;
import com.example.locklistener.lock_services.service.JobHandlerService;

import com.example.locklistener.lock_services.service.RemoteService;
import com.example.locklistener.lock_services.utils.KeepAliveUtils;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.locklistener.lock_services.service.LocalService;

/**
 * 进程保活管理
 */
public class KeepAliveManager {
    private static final String TAG = "KeepAliveManager";

    /**
     * 启动保活
     *
     * @param application            your application
     * @param runMode
     * @param foregroundNotification 前台服务
     */
    public static void toKeepAlive(@NonNull Application application, @NonNull int runMode, String title, String content, int res_icon, ForegroundNotification foregroundNotification) {
        if (KeepAliveUtils.isRunning(application)) {
            KeepAliveConfig.foregroundNotification = foregroundNotification;
            RunMode.setShape(runMode);
            KeepAliveConfig.runMode = RunMode.getShape();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //启动定时器，在定时器中启动本地服务和守护进程
                JobHandlerService.startJob(application);
                Log.e("gdscgsfsd", "toKeepAlive: " );
             //   Logger.INSTANCE.appendLog(application.getApplicationContext(),"KeepAliveManager toKeepAlive:");
               /* Intent localIntent = new Intent(application, LocalService.class);
                //启动守护进程
                Intent guardIntent = new Intent(application, RemoteService.class);
                if (Build.VERSION.SDK_INT >= 26) {

                    application.startForegroundService(localIntent);
                    application.startForegroundService(guardIntent);
                } else {
                    application.startService(localIntent);
                    application.startService(guardIntent);
                }*/
            } else {
                Intent localIntent = new Intent(application, LocalService.class);
                Intent guardIntent = new Intent(application, RemoteService.class);
                if (Build.VERSION.SDK_INT >= 26) {
                    application.startForegroundService(localIntent);
                    application.startForegroundService(guardIntent);
                } else {
                    application.startService(localIntent);
                    application.startService(guardIntent);
                }
            }
        }
    }

    public static void stopWork(Application application) {
        try {
            KeepAliveConfig.foregroundNotification = null;
            KeepAliveConfig.runMode = RunMode.getShape();
            JobHandlerService.stopJob();
            Intent localIntent = new Intent(application, LocalService.class);
            Intent guardIntent = new Intent(application, RemoteService.class);
            application.stopService(localIntent);
            application.stopService(guardIntent);
            application.stopService(new Intent(application, JobHandlerService.class));
        } catch (Exception e) {
            Log.e(TAG, "stopWork-->" + e.getMessage());
        }
    }


    public static void sendNotification(Context context, String title, String content, int icon, Intent intent2) {
        NotificationUtils.sendNotification(context, title, content, icon, intent2);
    }

    /**
     * 启动系统保活
     * @param cex
     */
    public static void launcherSyskeepAlive(Context cex){
        DevicesLaunchConfig.launchSystemKeepAlive(cex);
    }

    /**
     * 启动电量优化
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void batteryOptimizations(Context context) {
        if (!isIgnoringBatteryOptimizations(context)) {
            requestIgnoreBatteryOptimizations(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean isIgnoringBatteryOptimizations(Context context) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return isIgnoring;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void requestIgnoreBatteryOptimizations(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
