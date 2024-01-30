package com.example.callll
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            createNotificationChannel(it)
            showNotification(it)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Schedule Notification"
            val descriptionText = "Notification for scheduled event"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("schedule_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, "schedule_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Don't Forget!")
            .setContentText("Your scheduled event is tomorrow!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, builder.build())
    }
}
