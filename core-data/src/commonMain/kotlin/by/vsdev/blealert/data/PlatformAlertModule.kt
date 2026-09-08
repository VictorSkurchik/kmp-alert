package by.vsdev.blealert.data

import org.koin.core.module.Module

/** Binds the platform-specific [by.vsdev.blealert.domain.AlertNotifier]/[by.vsdev.blealert.domain.NotificationPermissionManager]. */
expect val platformAlertModule: Module
