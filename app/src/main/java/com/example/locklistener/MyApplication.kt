package com.example.locklistener


import android.app.ActivityManager
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.*
import android.os.Process.killProcess
import android.os.Process.myPid
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.work.*
import com.example.locklistener.lock_services.KeepAliveManager
import com.example.locklistener.lock_services.config.ForegroundNotification
import com.example.locklistener.lock_services.config.RunMode
import com.example.locklistener.lock_services.receivers.OnepxReceiver
import com.example.locklistener.lock_services.service.LocalService
import java.util.*

class MyApplication : Application(), LifecycleEventObserver, ComponentCallbacks2 {
    override fun onCreate() {
        super.onCreate()
        startLockScreenServices()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun startLockScreenServices() {
        KeepAliveManager.toKeepAlive(
            applicationContext as Application,
            RunMode.HIGH_POWER_CONSUMPTION,
            "App Locker is active",
            " ",
            R.drawable.ic_launcher_background,
            ForegroundNotification()
            { context, intent ->
                Log.d("JOB-->240", " foregroundNotificationClick")
            }
        )
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                Thread.sleep(300)
                startLockScreenServices()
            }
            else -> {}
        }
    }
}