package by.vsdev.blealert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import by.vsdev.blealert.domain.NotificationPermissionManager
import by.vsdev.blealert.feature.dashboard.DashboardScreen
import by.vsdev.blealert.feature.monitoring.MonitoringScreen
import by.vsdev.blealert.feature.settings.SettingsScreen
import by.vsdev.blealert.ui.appshell.MainScaffold
import by.vsdev.blealert.ui.navigation.AppTab
import by.vsdev.blealert.ui.navigation.DashboardRoute
import by.vsdev.blealert.ui.navigation.MonitoringRoute
import by.vsdev.blealert.ui.navigation.SettingsRoute
import by.vsdev.blealert.ui.navigation.rememberAppBackStack
import by.vsdev.blealert.ui.navigation.toRoute
import by.vsdev.blealert.ui.theme.BleAlertTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun App() {
    val permissionManager = koinInject<NotificationPermissionManager>()
    LaunchedEffect(Unit) { permissionManager.requestPermission() }

    BleAlertTheme {
        val backStack = rememberAppBackStack()

        NavDisplay(
            backStack = backStack,
            onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<DashboardRoute> {
                    MainTab(AppTab.DASHBOARD, backStack) { modifier -> DashboardScreen(modifier) }
                }
                entry<MonitoringRoute> {
                    MainTab(AppTab.MONITORING, backStack) { modifier -> MonitoringScreen(modifier) }
                }
                entry<SettingsRoute> {
                    MainTab(AppTab.SETTINGS, backStack) { modifier -> SettingsScreen(modifier) }
                }
            },
        )
    }
}

@Composable
private fun MainTab(
    tab: AppTab,
    backStack: NavBackStack<NavKey>,
    content: @Composable (Modifier) -> Unit,
) {
    MainScaffold(
        title = stringResource(tab.titleRes),
        selectedTab = tab,
        onTabSelected = { selected -> if (selected != tab) backStack[backStack.lastIndex] = selected.toRoute() },
        content = content,
    )
}
