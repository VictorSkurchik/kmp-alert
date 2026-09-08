package by.vsdev.blealert.ui.navigation

import kmp_ble_alert.sharedui.generated.resources.Res
import kmp_ble_alert.sharedui.generated.resources.tab_dashboard
import kmp_ble_alert.sharedui.generated.resources.tab_monitoring
import kmp_ble_alert.sharedui.generated.resources.tab_settings
import org.jetbrains.compose.resources.StringResource

enum class AppTab(val titleRes: StringResource) {
    DASHBOARD(Res.string.tab_dashboard),
    MONITORING(Res.string.tab_monitoring),
    SETTINGS(Res.string.tab_settings),
}
