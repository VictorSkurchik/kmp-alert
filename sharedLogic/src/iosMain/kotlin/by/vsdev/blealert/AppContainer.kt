package by.vsdev.blealert

import by.vsdev.blealert.core.alert.IosAlertNotifier
import by.vsdev.blealert.core.alert.IosNotificationPermissionManager
import by.vsdev.blealert.core.alert.NotificationPermissionManager
import by.vsdev.blealert.core.notification.WebSocketNotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val alertHistory = AlertHistoryRepository()
    private val alertNotifier = IosAlertNotifier()

    val notificationPermissionManager: NotificationPermissionManager = IosNotificationPermissionManager()

    // iOS Simulator shares the host's network, so "localhost" reaches `:server` running on the
    // same Mac. A real device needs the host's LAN IP or an ngrok tunnel instead.
    private val notificationService = WebSocketNotificationService(
        scope = scope,
        backendUrl = "ws://localhost:8080/ws/alerts",
    )

    private val remoteAlertCoordinator = RemoteAlertCoordinator(
        scope = scope,
        notificationService = notificationService,
        alertNotifier = alertNotifier,
        alertHistory = alertHistory,
    ).also { it.start() }

    fun createMonitoringViewModel(): MonitoringViewModel =
        MonitoringViewModel(remoteAlertCoordinator, notificationPermissionManager)
}
