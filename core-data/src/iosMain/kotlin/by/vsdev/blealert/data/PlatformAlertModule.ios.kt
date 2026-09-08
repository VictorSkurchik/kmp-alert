package by.vsdev.blealert.data

import by.vsdev.blealert.domain.AlertNotifier
import by.vsdev.blealert.domain.NotificationPermissionManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformAlertModule: Module = module {
    single<AlertNotifier> { IosAlertNotifier() }
    single<NotificationPermissionManager> { IosNotificationPermissionManager() }
}
