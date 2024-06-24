package me.tbsten.prac.foregroundservice

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val NOTIFICATION_CHANNEL_ID = "my-service-notification-channel"

class MyService : LifecycleService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            startForegroundService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun startForegroundService() {
        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        createNotificationChannel(
            id = NOTIFICATION_CHANNEL_ID,
            name = "カウントアップするやーつー",
        )
        val notification =
            createNotification(
                channelId = NOTIFICATION_CHANNEL_ID,
                contentTitle = "start count up",
                contentText = "started count up",
            )

        ServiceCompat.startForeground(
            this,
            notificationId,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH,
        )

        createNotificationChannel(
            id = NOTIFICATION_CHANNEL_ID,
            name = "カウントアップするやーつー",
            importance = NotificationManager.IMPORTANCE_LOW,
        )

        lifecycleScope.launch {
            var count = 0
            while (true) {
                delay(5_000)
                notificationManager.notify(
                    notificationId,
                    createNotification(
                        channelId = NOTIFICATION_CHANNEL_ID,
                        contentTitle = "count: $count",
                        contentText = "counted up to $count",
                    )
                )
                Log.d("prac-foreground-service", "count:$count")
                count++
            }
        }
    }
}