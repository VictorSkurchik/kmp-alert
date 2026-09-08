package by.vsdev.blealert.core.alert

interface NotificationPermissionManager {
    fun hasPermission(): Boolean
    suspend fun requestPermission(): Boolean
}
