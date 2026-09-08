package by.vsdev.blealert.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

sealed interface Route : NavKey

@Serializable
data object DashboardRoute : Route

@Serializable
data object MonitoringRoute : Route

@Serializable
data object SettingsRoute : Route

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(DashboardRoute::class, DashboardRoute.serializer())
            subclass(MonitoringRoute::class, MonitoringRoute.serializer())
            subclass(SettingsRoute::class, SettingsRoute.serializer())
        }
    }
}

@Composable
fun rememberAppBackStack(): NavBackStack<NavKey> = rememberNavBackStack(navConfig, DashboardRoute)

fun AppTab.toRoute(): Route = when (this) {
    AppTab.DASHBOARD -> DashboardRoute
    AppTab.MONITORING -> MonitoringRoute
    AppTab.SETTINGS -> SettingsRoute
}
