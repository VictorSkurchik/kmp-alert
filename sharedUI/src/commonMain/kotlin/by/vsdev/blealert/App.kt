package by.vsdev.blealert

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import by.vsdev.blealert.ui.navigation.AppTab
import by.vsdev.blealert.ui.navigation.DashboardRoute
import by.vsdev.blealert.ui.navigation.MonitoringRoute
import by.vsdev.blealert.ui.navigation.SettingsRoute
import by.vsdev.blealert.ui.navigation.rememberAppBackStack
import by.vsdev.blealert.ui.navigation.toRoute
import by.vsdev.blealert.ui.screens.DashboardScreen
import by.vsdev.blealert.ui.screens.MonitoringScreen
import by.vsdev.blealert.ui.screens.SettingsScreen
import by.vsdev.blealert.ui.templates.MainScaffold
import by.vsdev.blealert.ui.theme.BleAlertTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun App(viewModel: MonitoringViewModel) {
    BleAlertTheme {
        val backStack = rememberAppBackStack()

        NavDisplay(
            backStack = backStack,
            onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<DashboardRoute> {
                    MainTab(AppTab.DASHBOARD, backStack) { modifier -> DashboardScreen(viewModel, modifier) }
                }
                entry<MonitoringRoute> {
                    MainTab(AppTab.MONITORING, backStack) { modifier -> MonitoringScreen(viewModel, modifier) }
                }
                entry<SettingsRoute> {
                    MainTab(AppTab.SETTINGS, backStack) { modifier -> SettingsScreen(viewModel, modifier) }
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
