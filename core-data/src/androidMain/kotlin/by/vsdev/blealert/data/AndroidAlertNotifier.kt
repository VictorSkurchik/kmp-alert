package by.vsdev.blealert.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import by.vsdev.blealert.domain.Alert
import by.vsdev.blealert.domain.AlertNotifier
import by.vsdev.blealert.domain.AlertSeverity

class AndroidAlertNotifier(private val context: Context) : AlertNotifier {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_CRITICAL, "Critical alerts", NotificationManager.IMPORTANCE_HIGH),
            )
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_INFO, "Alerts", NotificationManager.IMPORTANCE_DEFAULT),
            )
        }
    }

    override fun notify(alert: Alert) {
        val channelId = if (alert.severity == AlertSeverity.CRITICAL) CHANNEL_CRITICAL else CHANNEL_INFO
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(alert.type.name)
            .setContentText("Severity: ${alert.severity.name}")
            .setPriority(
                if (alert.severity == AlertSeverity.CRITICAL) {
                    NotificationCompat.PRIORITY_HIGH
                } else {
                    NotificationCompat.PRIORITY_DEFAULT
                },
            )
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(alert.id.hashCode(), notification)
    }

    private companion object {
        const val CHANNEL_CRITICAL = "alerts-critical"
        const val CHANNEL_INFO = "alerts-info"
    }
}
