package by.vsdev.blealert.core.alert

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

class IosAlertNotifier : AlertNotifier {

    override fun notify(alert: Alert) {
        val content = UNMutableNotificationContent().apply {
            setTitle(alert.type.name)
            setBody("Severity: ${alert.severity.name}")
        }
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 0.1,
            repeats = false,
        )
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = alert.id,
            content = content,
            trigger = trigger,
        )
        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request, null)
    }
}
