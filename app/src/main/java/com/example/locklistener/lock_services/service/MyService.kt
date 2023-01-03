package com.example.locklistener.lock_services.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.locklistener.MainActivity
import com.example.locklistener.R
import com.example.locklistener.SecondActivity
import com.example.locklistener.lock_services.config.KeepAliveConfig


class MyService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null

    companion object {
        val START_SERVICE = "start"
        val STOP_SERVICE = "stop"
        val FOREGROUND_SERVICE = "foreground"
        const val TAG = "MyService"

    }

    var phoneNumber: String? = null
    var IsDynamicDataRequired: String? = null
    var name: String? = null
    var isForeGroundService = false
    val CHANNEL_ID: String = "channelId"
    val TAG = "MyService"


    inner class LocalBinder : Binder() {
        fun getService(): MyService = this@MyService
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("1gf6d6g1f6dg", "onCreate- (MyService-100)-->:")
        isForeGroundService = false

        mFloatingView =
            LayoutInflater.from(this).inflate(R.layout.activity_mai2, null)
        val LAYOUT_FLAG: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )
        params.gravity =
            Gravity.CENTER_VERTICAL or Gravity.CENTER_VERTICAL //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        mWindowManager!!.addView(mFloatingView, params)


        /*
                mFloatingView!!.findViewById<View>(R.id.itemContainer)
                    .setOnTouchListener(object : View.OnTouchListener {
                        private var initialX = 0
                        private var initialY = 0
                        private var initialTouchX = 0f
                        private var initialTouchY = 0f
                        override fun onTouch(v: View, event: MotionEvent): Boolean {
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    //remember the initial position.
                                    initialX = params.x
                                    initialY = params.y
                                    //get the touch location
                                    initialTouchX = event.rawX
                                    initialTouchY = event.rawY
                                    return true
                                }
                                MotionEvent.ACTION_MOVE -> {
                                    //Calculate the X and Y coordinates of the view.
                                    */
        /*        params.x =
                                                initialX + (event.rawX - initialTouchX).toInt()*//*

                            params.y =
                                initialY + (event.rawY - initialTouchY).toInt()

                            //Update the layout with new X & Y coordinate
                            mWindowManager!!.updateViewLayout(mFloatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
*/


    }


    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val intentAction = intent?.action
        Log.e("1gf6d6g1f6dg", "onStartCommand- (MyService-351)-->:")
        try {
            val it = Intent(this, SecondActivity::class.java)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(it)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when (intentAction) {
            START_SERVICE -> {
                name = intent.getStringExtra("name")
                phoneNumber = intent.getStringExtra("mobile")
                IsDynamicDataRequired = intent.getStringExtra("IsDynamicDataRequired")
            }

            STOP_SERVICE -> stopService()
            FOREGROUND_SERVICE -> {

                name = intent.getStringExtra("name")
                phoneNumber = intent.getStringExtra("mobile")
                IsDynamicDataRequired = intent.getStringExtra("IsDynamicDataRequired")
                Log.e(
                    "2sca94xfwsx",
                    "onStartCommand- (MyService-339)-->:${intent.getStringExtra("mobile")}"
                )
                doForegroundThings()
            }

            else -> {

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    fun doForegroundThings() {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        isForeGroundService = true
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(KeepAliveConfig.DEF_ICONS)
            .setContentTitle("Activated CallerId Popup")
            .setContentText("")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notification = builder.build()
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(4, notification)
        }
        startForeground(4, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "test"
            val descriptionText = "test desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    override fun onDestroy() {
        Log.e("1gf6d6g1f6dg", "onDestroy- (MyService-435)-->:")
        if (mFloatingView != null) {
            if (mWindowManager != null && mFloatingView != null) {
                try {
                    mWindowManager?.removeView(mFloatingView)
                    mWindowManager = null
                } catch (e: IllegalArgumentException) {
                    Log.e("flag", "onDestroy- (MyService-425)-->:${e.cause}")
                }
            }
        } else {
            Log.e("fs4d1fsdfsa", "onDestroy- (MyService-410)-->:else ke ander")
        }
        super.onDestroy()
    }

    private fun stopService() {
        try {
            stopForeground(true)
            isForeGroundService = false
            stopSelf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


