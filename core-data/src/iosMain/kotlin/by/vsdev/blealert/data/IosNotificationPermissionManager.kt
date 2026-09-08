package by.vsdev.blealert.data

import by.vsdev.blealert.domain.NotificationPermissionManager
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

/**
 * iOS has no synchronous permission-status API, so [hasPermission] reflects the last known
 * result of [requestPermission] rather than querying the system directly.
 */
class IosNotificationPermissionManager : NotificationPermissionManager {

    private var granted = false

    override fun hasPermission(): Boolean = granted

    override suspend fun requestPermission(): Boolean = suspendCancellableCoroutine { continuation ->
        UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound,
        ) { success, _ ->
            granted = success
            continuation.resume(success)
        }
    }
}
