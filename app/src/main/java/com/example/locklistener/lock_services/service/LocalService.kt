package com.example.locklistener.lock_services.service


import android.app.Notification
import android.app.Service
import android.content.*
import android.media.MediaPlayer
import android.os.*
import android.text.TextUtils
import android.util.Log
import androidx.work.*
import com.android.keepalivetest.KeepAliveAidl
import com.example.locklistener.lock_services.KeepAliveRuning
import com.example.locklistener.lock_services.config.KeepAliveConfig
import com.example.locklistener.lock_services.config.NotificationUtils
import com.example.locklistener.lock_services.config.RunMode.HIGH_POWER_CONSUMPTION
import com.example.locklistener.lock_services.receivers.NotificationClickReceiver
import com.example.locklistener.lock_services.receivers.OnepxReceiver
import java.util.*

class LocalService : Service() {
    private var mOnepxReceiver: OnepxReceiver? = null
    private var screenStateReceiver: ScreenStateReceiver? = null
    private var serviceAliver: ServiceAliver? = null
    private var isPause = true
    private var mediaPlayer: MediaPlayer? = null
    private var mBilder: LocalBinder? = null
    private var handler: Handler? = null
    private val TAG = javaClass.simpleName
    private var mKeepAliveRuning: KeepAliveRuning? = null
    private var isMusicPlay = false
    lateinit var wakeLock: PowerManager.WakeLock
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            Log.e("gfidjgnhfiugfhf", "onServiceDisconnected: ")
            val remoteService = Intent(this@LocalService, RemoteService::class.java)
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(remoteService)
            } else {
                startService(remoteService)
            }
            val intent = Intent(this@LocalService, RemoteService::class.java)
            this@LocalService.bindService(
                intent, this,
                BIND_ABOVE_CLIENT
            )
            val pm = this@LocalService.getSystemService(POWER_SERVICE) as PowerManager
            val isScreenOn = pm.isScreenOn
            if (isScreenOn) {
                // sendBroadcast(Intent("_ACTION_SCREEN_ON"))
            } else {
                sendBroadcast(Intent("_ACTION_SCREEN_OFF"))
            }
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.e("gfidjgnhfiugfhf", "onServiceConnected: ")
            try {
                if (mBilder != null && KeepAliveConfig.foregroundNotification != null) {
                    val guardAidl = KeepAliveAidl.Stub.asInterface(service)
                    guardAidl.wakeUp(KeepAliveConfig.TITLE,
                        KeepAliveConfig.CONTENT,
                        KeepAliveConfig.DEF_ICONS
                    )
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("gfidjgnhfiugfhf", "：service created")
        val pm2 = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm2.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PeriSecure:MyWakeLock")
        val intentFilter1 = IntentFilter()
        intentFilter1.addAction("android.intent.action.SCREEN_ON")
        intentFilter1.priority = 999
        if (mBilder == null) {
            mBilder = LocalBinder()
        }
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        isPause = pm.isScreenOn
        if (handler == null) {
            handler = Handler()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBilder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        wakeLock.acquire(10000)
        KeepAliveConfig.runMode = HIGH_POWER_CONSUMPTION
        Log.e(TAG, "onStartCommand：" + KeepAliveConfig.runMode)

        if (mOnepxReceiver == null) {
            mOnepxReceiver = OnepxReceiver()
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.SCREEN_OFF")
        intentFilter.addAction("android.intent.action.SCREEN_ON")
        registerReceiver(mOnepxReceiver, intentFilter)
        Log.e("gifkuoyghmf", "onStartCommand: ")
        if (screenStateReceiver == null) {
            screenStateReceiver = ScreenStateReceiver()
        }

        if (serviceAliver == null) {
            serviceAliver = ServiceAliver()
        }
        val intentFilter2 = IntentFilter()
        intentFilter2.addAction("_ACTION_SCREEN_OFF")
        intentFilter2.addAction("_ACTION_SCREEN_ON")
        registerReceiver(screenStateReceiver, intentFilter2)

        val intentFilter3 = IntentFilter()
        intentFilter3.addAction("MUSIC_PLAY")
        intentFilter3.addAction("MUSIC_PAUSE")
        registerReceiver(serviceAliver, intentFilter3)
        shouDefNotify()
        try {
            val intent3 = Intent(this, RemoteService::class.java)
            this.bindService(intent3, connection, BIND_ABOVE_CLIENT)
        } catch (e: Exception) {
            Log.e("RemoteService--", e.message!!)
        }

        try {
            if (Build.VERSION.SDK_INT < 25) {
                startService(Intent(this, HideForegroundService::class.java))
                Log.e(TAG, "onStartCommand: ")
            }
        } catch (e: Exception) {
            Log.e("HideForegroundService--", e.message!!)
        }
        if (mKeepAliveRuning == null) mKeepAliveRuning = KeepAliveRuning()
        mKeepAliveRuning!!.onRuning()
        return START_STICKY
    }

    private fun play() {
        Log.e(TAG, "music play")
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
        }
    }

    private fun pause() {
        Log.e(TAG, "pause- (LocalService-205)-->:music pause")
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            isMusicPlay = false
            isPause = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        unregisterReceiver(mOnepxReceiver)
        unregisterReceiver(screenStateReceiver)
        unregisterReceiver(serviceAliver)
        if (mKeepAliveRuning != null) {
            mKeepAliveRuning?.onStop()
        }
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer = null
        }
    }

    private fun shouDefNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(KeepAliveConfig.TITLE) && !TextUtils.isEmpty(KeepAliveConfig.CONTENT)) {
                val intent2 = Intent(applicationContext, NotificationClickReceiver::class.java)
                intent2.action = NotificationClickReceiver.CLICK_NOTIFICATION
                val notification: Notification = NotificationUtils.createNotification(
                    this@LocalService,
                    KeepAliveConfig.TITLE,
                    KeepAliveConfig.CONTENT,
                    KeepAliveConfig.DEF_ICONS,
                    intent2
                )
                startForeground(KeepAliveConfig.FOREGROUD_NOTIFICATION_ID, notification)
                Log.e("JOB-->237", TAG + "Show notification bar")
            }
        }
    }

    private inner class ScreenStateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "_ACTION_SCREEN_OFF") {
                isPause = false
                isMusicPlay = true
                play()
            } else if (intent.action == "_ACTION_SCREEN_ON") {
                isPause = true
                isMusicPlay = false
                pause()
            }
        }
    }

    private inner class ServiceAliver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e(TAG, "onReceive- (LocalService-276)-->isMusicPlay = $isMusicPlay:${intent.action}")
            if (isMusicPlay) {
                pause()
            } else {
                play()
            }
        }
    }

    private inner class LocalBinder : KeepAliveAidl.Stub() {
        @Throws(RemoteException::class)
        override fun wakeUp(title: String, discription: String, iconRes: Int) {
            shouDefNotify()
        }
    }


}