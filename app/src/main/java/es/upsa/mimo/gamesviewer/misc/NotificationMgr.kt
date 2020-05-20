package es.upsa.mimo.gamesviewer.misc

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.gamesviewer.R
import kotlinx.coroutines.CoroutineScope


class NotificationMgr(private val context: Context)
{
    fun sendNotification(juegoFav: JuegoFav, msTime: Long)
    {
        val notificationBuilder = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(context.getString(R.string.game_release_today))
            .setContentText(context.getString(R.string.game_release_name, juegoFav.name))
            .setWhen(System.currentTimeMillis());

        scheduleNotification(juegoFav.id, notificationBuilder.build(), msTime);
    }

    fun scheduleNotification(id: Int, notification: Notification, delay: Long)
    {
        val notificationIntent = Intent(context, NotificationPublisher::class.java);
        notificationIntent.putExtra(NotificationPublisher.notificationIdKey, id);
        notificationIntent.putExtra(NotificationPublisher.notificationKey, notification);
        val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        val futureInMillis = SystemClock.elapsedRealtime() + delay;
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    fun cancelAllNotifications()
    {
        val notificationIntent = Intent(context, NotificationPublisher::class.java);
        val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmManager.cancel(pendingIntent);
    }
}
