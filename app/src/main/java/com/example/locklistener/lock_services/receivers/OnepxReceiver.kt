package com.example.locklistener.lock_services.receivers


import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.locklistener.MyApplication
import com.example.locklistener.SecondActivity
import com.example.locklistener.lock_services.service.MyService
import com.example.locklistener.lock_services.utils.KeepAliveUtils

class OnepxReceiver : BroadcastReceiver() {
    var mHander1: Handler
    var appIsForeground = false
    var mService: MyService? = null
    var myContext: Context? = null
    var mBound = false
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            val binder: MyService.LocalBinder = service as MyService.LocalBinder
            mService = binder.getService()
            mBound = true
            bringServiceToForeground()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("fjdfmodf", "onReceive: ${intent.action}")
        Log.e("fkdjiogfmid", "onReceive: ${KeepAliveUtils.IsForeground(context)}")
        myContext = context
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
                startService(context)
                context.sendBroadcast(Intent("_ACTION_SCREEN_ON"))
            }

        }
    }

    fun startService(context: Context){
        if (isMyServiceRunning(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent2 =
                    Intent(context, MyService::class.java)
                intent2.action = MyService.STOP_SERVICE
                ContextCompat.startForegroundService(
                    context,
                    intent2
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val intent1 =
                        Intent(context, MyService::class.java)
                    intent1.putExtra("mobile", "9131414139")
                    intent1.putExtra("name", "monu meena")
                    intent1.action = MyService.FOREGROUND_SERVICE

                    ContextCompat.startForegroundService(context, intent1)

                } else {
                    val intent3 = Intent(context, MyService::class.java)
                    intent3.putExtra("mobile", "9131414139")
                    intent3.putExtra("name", "monu meena")
                    intent3.action = MyService.START_SERVICE
                    context.startService(intent3)
                    bindWithService()
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent1 =
                    Intent(context, MyService::class.java)
                intent1.putExtra("mobile", "9131414139")
                intent1.putExtra("name", "monu meena")
                intent1.action = MyService.FOREGROUND_SERVICE
                ContextCompat.startForegroundService(context, intent1)

            } else {
                val intent3 =
                    Intent(context, MyService::class.java)
                intent3.putExtra("mobile", "9131414139")
                intent3.putExtra("name", "monu meena")
                intent3.action = MyService.START_SERVICE
                context.startService(intent3)
                bindWithService()
            }
        }
    }


    private fun bringServiceToForeground() {
        mService?.let {
            if (!it.isForeGroundService) {
                val intent = Intent(myContext, MyService::class.java)
                intent.action = MyService.FOREGROUND_SERVICE
                if (myContext != null) {
                    ContextCompat.startForegroundService(myContext!!, intent)
                }
                mService!!.doForegroundThings()
            } else {
                Log.d("fdfdfd", "Service is already in foreground")
            }
        } ?: Log.d("fdfdfd", "Service is null")

    }

    private fun bindWithService() {
        val intent = Intent(myContext, MyService::class.java)
        MyApplication.getAppContext()
            ?.bindService(intent, connection, AppCompatActivity.BIND_IMPORTANT)
    }

    private fun isMyServiceRunning(context: Context): Boolean {
        val manager =
            context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (MyService::class.java.getName() == service.service.className) {
                return true
            }
        }
        return false
    }

    init {
        mHander1 = Handler(Looper.getMainLooper())
    }
}