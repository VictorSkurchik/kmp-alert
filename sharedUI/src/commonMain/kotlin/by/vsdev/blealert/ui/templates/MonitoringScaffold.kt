package by.vsdev.blealert.ui.templates

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MonitoringScaffold(title: String, content: @Composable (Modifier) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) },
    ) { padding ->
        content(Modifier.padding(padding))
    }
}
