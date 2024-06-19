package me.tbsten.prac.foregroundservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

fun Context.createNotificationChannel(
    id: String,
    name: String,
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
): NotificationChannel? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(
                id,
                name,
                importance,
            )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)

        return channel
    }
    return null
}

fun Context.createNotification(
    channelId: String,
    contentTitle: String,
    contentText: String,
    autoCancel: Boolean = false,
    ongoing: Boolean = true,
    @DrawableRes smallIcon: Int = R.mipmap.ic_launcher,
    contentIntent: PendingIntent = startMainActivityPendingIntent(),
    onlyAlertOnce: Boolean = true,
) = NotificationCompat.Builder(this, channelId)
    .setContentTitle(contentTitle)
    .setContentText(contentText)
    .setAutoCancel(autoCancel)
    .setOngoing(ongoing)
    .setSmallIcon(smallIcon)
    .setContentIntent(contentIntent)
    .setOnlyAlertOnce(onlyAlertOnce)
    .build()

const val REQUEST_CODE_START_MAIN_ACTIVITY = 420
fun Context.startMainActivityPendingIntent() = PendingIntent.getActivity(
    applicationContext,
    REQUEST_CODE_START_MAIN_ACTIVITY,
    Intent(applicationContext, MainActivity::class.java).apply {
        this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    },
    PendingIntent.FLAG_IMMUTABLE,
)
