package by.vsdev.blealert

import android.content.Context
import by.vsdev.blealert.core.alert.AndroidAlertNotifier
import by.vsdev.blealert.core.alert.AndroidNotificationPermissionManager
import by.vsdev.blealert.core.alert.NotificationPermissionManager
import by.vsdev.blealert.core.ble.BleAlertClient
import by.vsdev.blealert.core.ble.BleScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Process-wide singleton so MainActivity's UI and BleMonitoringForegroundService share the same
 * MonitoringRepository/BLE session instead of each running an independent one.
 */
class AppContainer private constructor(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val alertHistory = AlertHistoryRepository()
    private val alertNotifier = AndroidAlertNotifier(context)

    val notificationPermissionManager: NotificationPermissionManager =
        AndroidNotificationPermissionManager(context)

    val monitoringRepository = MonitoringRepository(
        scope = scope,
        scanner = BleScanner(),
        bleClient = BleAlertClient(),
        alertNotifier = alertNotifier,
        alertHistory = alertHistory,
    )

    fun createMonitoringViewModel(): MonitoringViewModel = MonitoringViewModel(monitoringRepository)

    companion object {
        @Volatile
        private var instance: AppContainer? = null

        fun getInstance(context: Context): AppContainer =
            instance ?: synchronized(this) {
                instance ?: AppContainer(context.applicationContext).also { instance = it }
            }
    }
}
