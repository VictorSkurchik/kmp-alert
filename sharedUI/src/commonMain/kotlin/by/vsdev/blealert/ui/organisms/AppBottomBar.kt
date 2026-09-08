package by.vsdev.blealert.ui.organisms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import by.vsdev.blealert.ui.navigation.AppTab
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppBottomBar(selectedTab: AppTab, onTabSelected: (AppTab) -> Unit) {
    NavigationBar {
        AppTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(imageVector = tab.icon(), contentDescription = null) },
                label = { Text(stringResource(tab.titleRes)) },
                alwaysShowLabel = true,
            )
        }
    }
}

private fun AppTab.icon(): ImageVector = when (this) {
    AppTab.DASHBOARD -> Icons.Filled.Dashboard
    AppTab.MONITORING -> Icons.Filled.MonitorHeart
    AppTab.SETTINGS -> Icons.Filled.Settings
}
