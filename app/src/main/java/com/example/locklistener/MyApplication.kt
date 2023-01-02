package com.example.locklistener


import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.locklistener.lock_services.KeepAliveManager
import com.example.locklistener.lock_services.config.ForegroundNotification
import com.example.locklistener.lock_services.config.RunMode

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
                startLockScreenServices()
            }
            else -> {}
        }
    }
}

