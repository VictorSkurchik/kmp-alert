# CLAUDE.md

## Архитектура

Kotlin Multiplatform + Compose Multiplatform, Clean Architecture (core `domain`/`data`/`ui` +
`feature-*` модули), Koin для DI, SOLID. UI и навигация — общие для Android и iOS: единый
Compose Multiplatform слой поверх Navigation 3 (`org.jetbrains.androidx.navigation3`).
iOS-приложение — тонкая SwiftUI-обвязка (`iOSApp.swift`/`ComposeView.swift`), которая только
инициализирует Koin (`KoinKt.doInitKoin`) и хостит Compose-контент через `MainViewController()`
(`ComposeUIViewController`) — весь UI-стейт и DI остаются внутри Compose/Koin и не пересекают
границу Swift, поэтому SKIE не используется вовсе.

Технология Wi-Fi Sensing полностью симулируется на бэкенде (модуль `server`) — никакого
физического датчика нет. Критичные алерты доставляются клиентам по постоянному WebSocket-
соединению с подтверждением получения (ACK); если клиент не подтвердил алерт за N секунд,
сервер дублирует его в Telegram-бота как гарантированный fallback-канал (устойчивость к
недоступности FCM/APNs).

Модули:
- `androidApp` — Android-приложение: `BleAlertApplication` стартует Koin, `MainActivity` —
  только разрешения на уведомления и `setContent { App() }`.
- `iosApp` — Xcode-проект, тонкая обвязка над Compose Multiplatform UI, линкует фреймворк `SharedUI`.
- `core-domain` — чистый domain-слой: `Alert`/`AlertType`/`AlertSeverity` (`@Serializable`,
  общий wire-контракт с `server`, единственный модуль с `jvm()`-таргетом ради этого),
  `ConnectionState`, интерфейсы `AlertRepository`/`AlertNotifier`/`NotificationPermissionManager`.
  Не знает ни про Android/iOS, ни про способ доставки алертов.
- `core-data` — реализация `core-domain`: `AlertRepositoryImpl` (WebSocket-клиент на Ktor,
  реконнект с backoff, ACK-протокол), платформенные `AlertNotifier`/`NotificationPermissionManager`
  (Android/iOS), Koin-модули (`dataModule`, `platformAlertModule`). `NotificationService` —
  внутренняя деталь реализации, наружу торчит только `AlertRepository`.
- `core-ui` — общие Compose-компоненты, используемые ≥2 фичами (тема, `AlertRow`,
  `ConnectionStatusBar` и т.д.); зависит от `core-domain`. Единственный модуль с
  `compose.resources { publicResClass = true }` — его строки (`no_alerts_yet`,
  `alert_type_*`, `connection_state_*` и т.д.) видны feature-модулям.
- `feature-dashboard`, `feature-monitoring`, `feature-settings` — по одному экрану на модуль,
  каждый со своей ViewModel и Koin-модулем (`dashboardModule`/`monitoringModule`/
  `settingsModule`), presentation-only — данные и домен всегда общие (`core-domain`/`core-data`),
  отдельного data/domain-слоя внутри фичи нет, так как ни у одной из них нет уникального
  источника данных.
- `sharedUI` — тонкий composition root: `App.kt` (Navigation 3 `NavDisplay`, резолвит
  ViewModel'и фич через `koinViewModel()`), `di/Koin.kt` (`initKoin`, собирает модули всех
  core/feature-модулей), `ui/appshell/` (`AppBottomBar`/`MainScaffold`), `ui/navigation/`
  (`Route`/`AppTab`). Единственный модуль, который iOS импортирует напрямую (framework
  `SharedUI`, без `export()` — Swift вызывает только `MainViewController()`/`doInitKoin`).
  Локализация — через `compose.components.resources` (`values/strings.xml` = русский по
  умолчанию, `values-en/strings.xml` = английский по системной локали устройства); у каждого
  модуля со своими строками — свой набор `values{,-en}/strings.xml`.
- `server` — Ktor-бэкенд (JVM, Netty): симулятор Wi-Fi Sensing событий, WebSocket-рассылка
  с ACK-таймаутом, Telegram-fallback. Не входит в KMP-таргеты клиентов, зависит только от
  `core-domain`. Настройки (`TELEGRAM_BOT_TOKEN`/`TELEGRAM_CHAT_ID`) — только через переменные
  окружения/`server/.env` (в `.gitignore`, см. `server/.env.example`), никогда не в исходниках.

`core-domain`/`core-data`/`core-ui` и `feature-*` не знают про Android/iOS приложения — только
`sharedUI` объединяет их в готовое приложение через Koin.

BLE-стек (`core-ble`, экран Scan) и Python-демон на Asus Tinker Board (`tinkerboard-daemon/`)
удалены из репозитория — полностью заменены backend-симуляцией Wi-Fi Sensing.

Тесты в первой итерации не пишем.

## Git: коммиты

Никогда не добавлять агента в соавторы коммита. Не добавлять trailer вида
`Co-Authored-By: Claude ...` — коммиты оформляются только от имени автора.

Сообщения коммитов — Conventional Commits: `<type>(<scope>): <субъект>`, тип обязателен,
scope — опционален.

Допустимые типы:

| Тип      | Когда использовать                                  |
|----------|------------------------------------------------------|
| feat     | новая функциональность                                |
| fix      | исправление бага                                      |
| docs     | только документация (README, комментарии, CLAUDE.md)  |
| style    | форматирование, без изменения логики                  |
| refactor | изменение кода без изменения поведения                |
| perf     | улучшение производительности                          |
| test     | добавление/правка тестов                              |
| build    | сборка, зависимости, Gradle-конфигурация               |
| ci       | CI/CD конфигурация                                    |
| chore    | всё остальное (init, обслуживание репозитория)         |
| revert   | откат предыдущего коммита                              |

## Git: branching

- `main` — стабильная ветка, отражает выпущенное состояние.
- `develop` — основная ветка интеграции, все фичи сливаются сюда.
- `feature/<название>` — ветка фичи, ответвляется от `develop` и вливается обратно в `develop`.

Сейчас (ранняя стадия проекта): фича-ветки заливаются в `develop` напрямую, без PR. Это
временная политика для старта проекта, а не постоянное правило.

## Версионирование клиентов

`versionName` — SemVer: `MAJOR.MINOR.PATCH` (например `1.0.0`).

Тип сборки добавляет суффикс к версии:
- debug-сборка → суффикс `-debug`
- release-сборка → суффикс `-release`

Итоговая версия — это база + суффикс флейвора + суффикс типа сборки, например
`1.0.0-dev-debug`, `1.0.0-release`.

## Product flavors (Android)

Три флейвора по окружению — `dev`, `stage`, `prod`:

- `dev` и `stage` получают свой `applicationIdSuffix` и `versionNameSuffix`, чтобы ставиться
  на устройство одновременно с `prod` без конфликта.
- `prod` — целевой публичный `applicationId`, без суффикса.

Конфигурация — в `androidApp/build.gradle.kts` (`flavorDimensions`/`productFlavors`).

## Лицензия

Проект распространяется под Apache License 2.0 (`LICENSE` в корне репозитория).
