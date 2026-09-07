package by.vsdev.blealert

import by.vsdev.blealert.core.alert.IosAlertNotifier
import by.vsdev.blealert.core.alert.IosNotificationPermissionManager
import by.vsdev.blealert.core.alert.NotificationPermissionManager
import by.vsdev.blealert.core.ble.BleAlertClient
import by.vsdev.blealert.core.ble.BleScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val alertHistory = AlertHistoryRepository()
    private val alertNotifier = IosAlertNotifier()

    val notificationPermissionManager: NotificationPermissionManager = IosNotificationPermissionManager()

    val monitoringRepository = MonitoringRepository(
        scope = scope,
        scanner = BleScanner(),
        bleClient = BleAlertClient(),
        alertNotifier = alertNotifier,
        alertHistory = alertHistory,
    )

    fun createMonitoringViewModel(): MonitoringViewModel = MonitoringViewModel(monitoringRepository)
}
