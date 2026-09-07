package by.vsdev.blealert.ui.organisms

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import by.vsdev.blealert.ui.navigation.AppTab

@Composable
fun AppBottomBar(selectedTab: AppTab, onTabSelected: (AppTab) -> Unit) {
    NavigationBar {
        AppTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                icon = {},
                label = { Text(tab.title) },
                alwaysShowLabel = true,
            )
        }
    }
}
