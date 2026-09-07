package by.vsdev.blealert

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import by.vsdev.blealert.core.ble.BleDevice
import by.vsdev.blealert.ui.navigation.AppTab
import by.vsdev.blealert.ui.screens.DashboardScreen
import by.vsdev.blealert.ui.screens.MonitoringScreen
import by.vsdev.blealert.ui.screens.ScanScreen
import by.vsdev.blealert.ui.screens.SettingsScreen
import by.vsdev.blealert.ui.templates.MainScaffold

private sealed class AppScreen {
    data object Scan : AppScreen()
    data object Main : AppScreen()
}

@Composable
fun App(viewModel: MonitoringViewModel) {
    MaterialTheme {
        var screen by remember { mutableStateOf<AppScreen>(AppScreen.Scan) }
        var tab by remember { mutableStateOf(AppTab.DASHBOARD) }

        when (screen) {
            AppScreen.Scan -> ScanScreen(
                viewModel = viewModel,
                onDeviceSelected = { device: BleDevice ->
                    viewModel.connect(device)
                    screen = AppScreen.Main
                },
            )
            AppScreen.Main -> MainScaffold(
                title = tab.title,
                selectedTab = tab,
                onTabSelected = { tab = it },
            ) { modifier ->
                when (tab) {
                    AppTab.DASHBOARD -> DashboardScreen(viewModel = viewModel, modifier = modifier)
                    AppTab.MONITORING -> MonitoringScreen(viewModel = viewModel, modifier = modifier)
                    AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel, modifier = modifier)
                }
            }
        }
    }
}
