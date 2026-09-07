package by.vsdev.blealert.ui.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import by.vsdev.blealert.core.alert.Alert
import by.vsdev.blealert.ui.molecules.AlertRow

@Composable
fun AlertHistoryList(alerts: List<Alert>, modifier: Modifier = Modifier) {
    if (alerts.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No alerts yet", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }
    LazyColumn(modifier = modifier) {
        items(alerts.asReversed(), key = { it.id }) { alert ->
            AlertRow(alert = alert)
        }
    }
}
