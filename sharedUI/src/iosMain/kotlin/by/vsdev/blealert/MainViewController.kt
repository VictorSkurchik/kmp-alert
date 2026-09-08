package by.vsdev.blealert

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(viewModel: MonitoringViewModel): UIViewController =
    ComposeUIViewController { App(viewModel = viewModel) }
