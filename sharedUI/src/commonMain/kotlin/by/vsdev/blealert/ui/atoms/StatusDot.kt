package by.vsdev.blealert.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusDot(color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(10.dp).background(color, CircleShape))
}

object StatusColors {
    val connected: Color @Composable get() = MaterialTheme.colorScheme.primary
    val connecting: Color @Composable get() = MaterialTheme.colorScheme.tertiary
    val disconnected: Color @Composable get() = MaterialTheme.colorScheme.error
    val idle: Color @Composable get() = MaterialTheme.colorScheme.outline
}
