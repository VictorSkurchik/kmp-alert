package by.vsdev.blealert

import android.content.Context
import by.vsdev.blealert.core.alert.AndroidAlertNotifier
import by.vsdev.blealert.core.alert.AndroidNotificationPermissionManager
import by.vsdev.blealert.core.alert.NotificationPermissionManager
import by.vsdev.blealert.core.ble.BleAlertClient
import by.vsdev.blealert.core.ble.BleScanner
import by.vsdev.blealert.core.notification.WebSocketNotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Process-wide singleton so MainActivity's UI shares the same [RemoteAlertCoordinator] instance.
 *
 * [monitoringRepository] is BLE-only dead code, no longer wired into the app's data flow - it's
 * kept here purely so [BleMonitoringForegroundService] (which is never started anymore, see
 * MainActivity) still compiles without changes.
 */
class AppContainer private constructor(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val alertHistory = AlertHistoryRepository()
    private val alertNotifier = AndroidAlertNotifier(context)

    val notificationPermissionManager: NotificationPermissionManager =
        AndroidNotificationPermissionManager(context)

    // BLE dead code, retained only for BleMonitoringForegroundService's sake - see class doc.
    val monitoringRepository = MonitoringRepository(
        scope = scope,
        scanner = BleScanner(),
        bleClient = BleAlertClient(),
        alertNotifier = alertNotifier,
        alertHistory = alertHistory,
    )

    // Android emulator loopback to the host machine running `:server`. For a real device, point
    // this at the host's LAN IP or an ngrok tunnel instead.
    private val notificationService = WebSocketNotificationService(
        scope = scope,
        backendUrl = "ws://10.0.2.2:8080/ws/alerts",
    )

    private val remoteAlertCoordinator = RemoteAlertCoordinator(
        scope = scope,
        notificationService = notificationService,
        alertNotifier = alertNotifier,
        alertHistory = alertHistory,
    ).also { it.start() }

    fun createMonitoringViewModel(): MonitoringViewModel =
        MonitoringViewModel(remoteAlertCoordinator, notificationPermissionManager)

    companion object {
        @Volatile
        private var instance: AppContainer? = null

        fun getInstance(context: Context): AppContainer =
            instance ?: synchronized(this) {
                instance ?: AppContainer(context.applicationContext).also { instance = it }
            }
    }
}
