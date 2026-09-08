package by.vsdev.blealert.domain

interface AlertNotifier {
    fun notify(alert: Alert)
}
