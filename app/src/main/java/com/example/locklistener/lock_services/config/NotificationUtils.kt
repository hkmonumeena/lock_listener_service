package com.example.locklistener.lock_services.config


import android.app.Notification
import android.content.ContextWrapper
import android.app.NotificationManager
import android.app.NotificationChannel
import androidx.annotation.RequiresApi
import android.os.Build
import android.content.Intent
import android.app.PendingIntent
import android.content.Context
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.example.locklistener.R
import java.util.*


class NotificationUtils private constructor(private val mContext: Context) : ContextWrapper(
    mContext) {
    private var manager: NotificationManager? = null
        private get() {
            if (field == null) {
                field = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return field
        }
    private val id: String
    private val name: String
    private var channel: NotificationChannel? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        if (channel == null) {
            channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            channel!!.enableVibration(false)
            channel!!.enableLights(false)
            channel!!.enableVibration(false)
            channel!!.vibrationPattern = longArrayOf(0)
            channel!!.setSound(null, null)
            manager!!.createNotificationChannel(channel!!)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getChannelNotification(
        title: String?,
        content: String?,
        icon: Int,
        intent: Intent?
    ): Notification.Builder {
        //PendingIntent.FLAG_UPDATE_CURRENT 这个类型才能传值
        var title = title
        var content = content
        var icon = icon
        val pendingIntent =
            PendingIntent.getBroadcast(mContext, 0, intent!!,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        if (TextUtils.isEmpty(title)) {
            title = mContext.applicationInfo.name
        }
        if (TextUtils.isEmpty(content)) {
            content = mContext.applicationInfo.name
        }
        if (icon == 0) {
            icon = R.mipmap.ic_launcher
        }
        return Notification.Builder(mContext, id)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }

    fun getNotification_25(
        title: String?,
        content: String?,
        icon: Int,
        intent: Intent?
    ): NotificationCompat.Builder {
        //PendingIntent.FLAG_UPDATE_CURRENT 这个类型才能传值
        val pendingIntent =
            PendingIntent.getBroadcast(mContext, 0, intent!!, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(mContext, id)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0))
            .setContentIntent(pendingIntent)
    }

    companion object {
        @JvmStatic
        fun sendNotification(
            context: Context,
            title: String,
            content: String,
            icon: Int,
            intent: Intent
        ) {
            val notificationUtils = NotificationUtils(context)
            var notification: Notification? = null
            notification = if (Build.VERSION.SDK_INT >= 26) {
                notificationUtils.createNotificationChannel()
                notificationUtils.getChannelNotification(title, content, icon, intent).build()
            } else {
                notificationUtils.getNotification_25(title, content, icon, intent).build()
            }
            notificationUtils.manager!!.notify(Random().nextInt(10000), notification)
        }

        @JvmStatic
        fun createNotification(
            context: Context,
            title: String,
            content: String,
            icon: Int,
            intent: Intent
        ): Notification {
            val notificationUtils = NotificationUtils(context)
            var notification: Notification? = null
            notification = if (Build.VERSION.SDK_INT >= 26) {
                notificationUtils.createNotificationChannel()
                notificationUtils.getChannelNotification(title, content, icon, intent).build()
            } else {
                notificationUtils.getNotification_25(title, content, icon, intent).build()
            }
            return notification
        }
    }

    init {
        id = mContext.packageName
        name = mContext.packageName
    }
}