package com.example.locklistener.lock_services.receivers


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.locklistener.SecondActivity
import com.example.locklistener.lock_services.utils.KeepAliveUtils
import java.lang.Exception

class OnepxReceiver : BroadcastReceiver() {
    var mHander1: Handler
    var appIsForeground = false
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("fjdfmodf", "onReceive: ${intent.action}")
        Log.e("fkdjiogfmid", "onReceive: ${KeepAliveUtils.IsForeground(context)}")
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            appIsForeground = KeepAliveUtils.IsForeground(context)
            try {
                val it = Intent(context, SecondActivity::class.java)
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
               // context.startActivity(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            context.sendBroadcast(Intent("_ACTION_SCREEN_OFF"))
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            if (!appIsForeground) {
                appIsForeground = false
                context.sendBroadcast(Intent("_ACTION_SCREEN_ON"))
            }

        }
    }

    init {
        mHander1 = Handler(Looper.getMainLooper())
    }
}