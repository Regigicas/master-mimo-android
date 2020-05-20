package es.upsa.mimo.gamesviewer.misc

import android.app.Notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context

import android.content.Intent


class NotificationPublisher : BroadcastReceiver()
{
    companion object
    {
        var notificationIdKey = "notificationId"
        var notificationKey = "notification"
    }

    override fun onReceive(context: Context, intent: Intent)
    {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = intent.getParcelableExtra(notificationKey) as Notification
        val id = intent.getIntExtra(notificationIdKey, 0)
        notificationManager.notify(id, notification)
    }
}
