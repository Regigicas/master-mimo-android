package es.upsa.mimo.gamesviewer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationExtend : Application()
{
    override fun onCreate()
    {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val name = getString(R.string.channel_name);
            val descriptionText = getString(R.string.channel_description);
            val importance = NotificationManager.IMPORTANCE_DEFAULT;
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.description = descriptionText;
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager;
            notificationManager.createNotificationChannel(channel);
        }
    }
}
