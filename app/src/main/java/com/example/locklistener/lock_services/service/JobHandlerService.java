package com.example.locklistener.lock_services.service;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.locklistener.lock_services.config.KeepAliveConfig;
import com.example.locklistener.lock_services.utils.KeepAliveUtils;

import androidx.annotation.RequiresApi;

@SuppressLint("SpecifyJobSchedulerIdRange")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class JobHandlerService extends JobService {
    private String TAG = this.getClass().getSimpleName();
    private static JobScheduler mJobScheduler;

    public static void startJob(Context context) {
        try {
            mJobScheduler = (JobScheduler) context.getSystemService(
                    Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(10,
                    new ComponentName(context.getPackageName(),
                            JobHandlerService.class.getName())).setPersisted(true);
            /**
             * I was having this problem and after review some blogs and the official documentation,
             * I realised that JobScheduler is having difference behavior on Android N(24 and 25).
             * JobScheduler works with a minimum periodic of 15 mins.
             *
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //7.0 The above is executed with a delay of 1s
                builder.setMinimumLatency(KeepAliveConfig.JOB_TIME);
            } else {
                //Execute the job every 1s
                builder.setPeriodic(KeepAliveConfig.JOB_TIME);
            }
            mJobScheduler.schedule(builder.build());

        } catch (Exception e) {
            Log.e("startJob-> 69 ", e.getMessage());
        }
    }

    public static void stopJob() {
        if (mJobScheduler != null)
            mJobScheduler.cancelAll();
    }

    private void startService(Context context) {
        try {
            Log.e(TAG, "82 ---》Start the dual-process keep-alive service");
            //start local service
            Intent localIntent = new Intent(context, LocalService.class);
            //start the daemon
            Intent guardIntent = new Intent(context, RemoteService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(localIntent);
                startForegroundService(guardIntent);
            } else {
                startService(localIntent);
                startService(guardIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @SuppressLint("SpecifyJobSchedulerIdRange")
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startJob(this);
            }
            if (!KeepAliveUtils.isServiceRunning(getApplicationContext(), getPackageName() + ":local") || !KeepAliveUtils.isRunningTaskExist(getApplicationContext(), getPackageName() + ":remote")) {
                Log.e("JOB--> 211", " restarted the service");
                startService(this);
            }
        } catch (Exception e) {
            Log.e("JOB--> 215", e.getMessage());

        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
       // Log.e("JOB--> 223", " Job Stop");
        if (!KeepAliveUtils.isServiceRunning(getApplicationContext(), getPackageName() + ":local") || !KeepAliveUtils.isRunningTaskExist(getApplicationContext(), getPackageName() + ":remote")) {
            startService(this);
        }
//        if (mCurrentTask != null) {
//            mCurrentTask.cancel(true);
//        }

        return true;
    }
}
