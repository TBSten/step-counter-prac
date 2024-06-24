package me.tbsten.prac.foregroundservice

import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope

private const val NOTIFICATION_CHANNEL_ID = "my-service-notification-channel"

class MyService : LifecycleService() {
    private val counter = SensorStepCounter(this, lifecycleScope)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            startForegroundService()
        }
        counter.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        counter.finish()
    }

    private fun startForegroundService() {
        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        createNotificationChannel(
            id = NOTIFICATION_CHANNEL_ID,
            name = "歩数カウンター",
        )
        val notification =
            createNotification(
                channelId = NOTIFICATION_CHANNEL_ID,
                contentTitle = "歩数を計測中です",
            )

        ServiceCompat.startForeground(
            this,
            notificationId,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH,
        )
    }
}