# CLAUDE.md

## Architecture

Kotlin Multiplatform + Compose Multiplatform, Clean Architecture (core `domain`/`data`/`ui` +
`feature-*` modules), Koin for DI, SOLID. UI and navigation are shared between Android and iOS:
a single Compose Multiplatform layer on top of Navigation 3
(`org.jetbrains.androidx.navigation3`). The iOS app is a thin SwiftUI wrapper
(`iOSApp.swift`/`ComposeView.swift`) that only initializes Koin (`KoinKt.doInitKoin`) and hosts
the Compose content via `MainViewController()` (`ComposeUIViewController`) — all UI state and DI
stay inside Compose/Koin and never cross the Swift boundary, so SKIE is not used at all.

Wi-Fi Sensing technology is fully simulated on the backend (the `server` module) — there is no
physical sensor. Critical alerts are delivered to clients over a persistent WebSocket connection
with delivery acknowledgment (ACK); if a client doesn't ACK an alert within N seconds, the server
duplicates it to a Telegram bot as a guaranteed fallback channel (resilience against
FCM/APNs unavailability).

Modules:
- `androidApp` — Android app: `BleAlertApplication` starts Koin, `MainActivity` only handles the
  notification permission and `setContent { App() }`.
- `iosApp` — Xcode project, a thin wrapper over the shared Compose Multiplatform UI, links the
  `SharedUI` framework.
- `core-domain` — pure domain layer: `Alert`/`AlertType`/`AlertSeverity` (`@Serializable`, the
  shared wire contract with `server`, the only module with a `jvm()` target for that reason),
  `ConnectionState`, the `AlertRepository`/`AlertNotifier`/`NotificationPermissionManager`
  interfaces. Knows nothing about Android/iOS or how alerts are delivered.
- `core-data` — implementation of `core-domain`: `AlertRepositoryImpl` (Ktor-based WebSocket
  client, reconnect with backoff, ACK protocol), platform `AlertNotifier`/
  `NotificationPermissionManager` (Android/iOS), Koin modules (`dataModule`,
  `platformAlertModule`). `NotificationService` is an internal implementation detail — only
  `AlertRepository` is exposed.
- `core-ui` — shared Compose components used by ≥2 features (theme, `AlertRow`,
  `ConnectionStatusBar`, etc.); depends on `core-domain`. The only module with
  `compose.resources { publicResClass = true }` — its strings (`no_alerts_yet`,
  `alert_type_*`, `connection_state_*`, etc.) are visible to feature modules.
- `feature-dashboard`, `feature-monitoring`, `feature-settings` — one screen per module, each
  with its own ViewModel and Koin module (`dashboardModule`/`monitoringModule`/
  `settingsModule`), presentation-only — data and domain are always shared (`core-domain`/
  `core-data`); there's no separate data/domain layer inside a feature since none of them has a
  unique data source.
- `sharedUI` — thin composition root: `App.kt` (Navigation 3 `NavDisplay`, resolves feature
  ViewModels via `koinViewModel()`), `di/Koin.kt` (`initKoin`, aggregates all core/feature
  modules), `ui/appshell/` (`AppBottomBar`/`MainScaffold`), `ui/navigation/` (`Route`/`AppTab`).
  The only module iOS imports directly (the `SharedUI` framework, without `export()` — Swift
  only calls `MainViewController()`/`doInitKoin`). Localization goes through
  `compose.components.resources` (`values/strings.xml` = Russian by default, `values-en/
  strings.xml` = English, following the device's system locale); every module with its own
  strings has its own `values{,-en}/strings.xml` set.
- `server` — Ktor backend (JVM, Netty): Wi-Fi Sensing event simulator, WebSocket broadcast with
  ACK timeout, Telegram fallback. Not part of the clients' KMP targets, depends only on
  `core-domain`. Settings (`TELEGRAM_BOT_TOKEN`/`TELEGRAM_CHAT_ID`) come only from environment
  variables/`server/.env` (gitignored, see `server/.env.example`), never from source.

`core-domain`/`core-data`/`core-ui` and `feature-*` know nothing about the Android/iOS apps —
only `sharedUI` wires them together into a finished app through Koin.

The BLE stack (`core-ble`, the Scan screen) and the Python daemon for the Asus Tinker Board
(`tinkerboard-daemon/`) have been removed from the repository — fully replaced by the backend
Wi-Fi Sensing simulation.

We don't write tests in the first iteration.

## Git: commits

Never add the agent as a commit co-author. Do not add a
`Co-Authored-By: Claude ...` trailer — commits are authored solely by the human author.

Commit messages must be written in English.

Commit messages follow Conventional Commits: `<type>(<scope>): <subject>`, type is required,
scope is optional.

Allowed types:

| Type     | When to use                                    |
|----------|-------------------------------------------------|
| feat     | new functionality                                |
| fix      | bug fix                                          |
| docs     | documentation only (README, comments, CLAUDE.md) |
| style    | formatting, no logic change                      |
| refactor | code change without behavior change              |
| perf     | performance improvement                          |
| test     | adding/fixing tests                              |
| build    | build, dependencies, Gradle configuration        |
| ci       | CI/CD configuration                              |
| chore    | everything else (init, repo maintenance)         |
| revert   | reverting a previous commit                      |

## Git: branching

- `main` — stable branch, reflects the released state.
- `develop` — main integration branch, all features merge here.
- `feature/<name>` — feature branch, branches off `develop` and merges back into `develop`.

Currently (early project stage): feature branches are pushed directly into `develop`, without a
PR. This is a temporary policy for kicking off the project, not a permanent rule.

## Client versioning

`versionName` — SemVer: `MAJOR.MINOR.PATCH` (e.g. `1.0.0`).

The build type adds a suffix to the version:
- debug build → `-debug` suffix
- release build → `-release` suffix

The final version is the base plus the flavor suffix plus the build type suffix, e.g.
`1.0.0-dev-debug`, `1.0.0-release`.

## Product flavors (Android)

Three flavors by environment — `dev`, `stage`, `prod`:

- `dev` and `stage` get their own `applicationIdSuffix` and `versionNameSuffix` so they can be
  installed on a device alongside `prod` without conflicts.
- `prod` — the target public `applicationId`, no suffix.

Configuration lives in `androidApp/build.gradle.kts` (`flavorDimensions`/`productFlavors`).

## License

The project is distributed under the Apache License 2.0 (`LICENSE` at the repo root).
