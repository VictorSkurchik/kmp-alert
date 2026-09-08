package by.vsdev.blealert.data

import android.content.Context
import by.vsdev.blealert.domain.AlertNotifier
import by.vsdev.blealert.domain.NotificationPermissionManager
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Resolves the plain `Context` registered by `androidApp`'s `startKoin { androidContext(...) }`
 * via `get<Context>()` (plain koin-core API) rather than koin-android's `androidContext()` Scope
 * extension, so this module doesn't need a `koin-android` dependency of its own.
 */
actual val platformAlertModule: Module = module {
    single<AlertNotifier> { AndroidAlertNotifier(get<Context>()) }
    single<NotificationPermissionManager> { AndroidNotificationPermissionManager(get<Context>()) }
}
