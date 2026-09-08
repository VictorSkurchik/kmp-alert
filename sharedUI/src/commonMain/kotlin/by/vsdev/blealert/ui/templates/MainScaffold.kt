package by.vsdev.blealert.ui.templates

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.vsdev.blealert.ui.navigation.AppTab
import by.vsdev.blealert.ui.organisms.AppBottomBar

@Composable
fun MainScaffold(
    title: String,
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) },
        bottomBar = { AppBottomBar(selectedTab = selectedTab, onTabSelected = onTabSelected) },
    ) { padding ->
        content(Modifier.padding(padding))
    }
}
