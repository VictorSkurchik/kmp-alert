package by.vsdev.blealert.domain

interface NotificationPermissionManager {
    fun hasPermission(): Boolean
    suspend fun requestPermission(): Boolean
}
