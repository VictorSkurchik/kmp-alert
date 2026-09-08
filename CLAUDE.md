# CLAUDE.md

## Архитектура

Kotlin Multiplatform + Compose Multiplatform, Clean Architecture, модульная сборка, SOLID.
UI и навигация — общие для Android и iOS: единый Compose Multiplatform слой (atomic design —
atoms/molecules/organisms/templates/screens в `sharedUI`) поверх Navigation 3
(`org.jetbrains.androidx.navigation3`). iOS-приложение — тонкая SwiftUI-обвязка, которая хостит
Compose-контент через `ComposeUIViewController`; SKIE используется только там, где Swift всё ещё
напрямую вызывает Kotlin suspend-функции (например, запрос разрешения на уведомления при старте
приложения) — весь UI-стейт (`Flow`/`StateFlow`) остаётся внутри Compose и не пересекает границу
Swift.

Технология Wi-Fi Sensing полностью симулируется на бэкенде (модуль `server`) — никакого
физического датчика нет. Критичные алерты доставляются клиентам по постоянному WebSocket-
соединению с подтверждением получения (ACK); если клиент не подтвердил алерт за N секунд,
сервер дублирует его в Telegram-бота как гарантированный fallback-канал (устойчивость к
недоступности FCM/APNs).

Модули:
- `androidApp` — Android-приложение: DI-обвязка, разрешения, манифест.
- `iosApp` — Xcode-проект, тонкая обвязка над Compose Multiplatform UI, линкует фреймворк `SharedUI`.
- `sharedLogic` — доменный/оркестрирующий слой (реэкспортирует публичные API `core-ble`,
  `core-alert`, `core-notification`); framework для iOS больше не собирает — эту роль взял на
  себя `sharedUI`. `RemoteAlertCoordinator` — источник алертов для `MonitoringViewModel`.
- `sharedUI` — общий Compose Multiplatform UI для Android и iOS (atomic design, Navigation 3),
  единственный модуль, который iOS импортирует напрямую (реэкспортирует `sharedLogic`).
  Локализация — через `compose.components.resources` (`values/strings.xml` = русский по
  умолчанию, `values-en/strings.xml` = английский по системной локали устройства).
- `core-alert` — доменная модель алертов (`Alert`/`AlertType`/`AlertSeverity`, `@Serializable`),
  общий wire-контракт между клиентами и `server` (единственный модуль с `jvm()`-таргетом ради
  этого), плюс платформенные `AlertNotifier`/`NotificationPermissionManager`.
- `core-notification` — `NotificationService`: WebSocket-клиент (Ktor, `ktor-client-cio`) к
  `server` с реконнектом и ACK-протоколом.
- `server` — Ktor-бэкенд (JVM, Netty): симулятор Wi-Fi Sensing событий, WebSocket-рассылка
  с ACK-таймаутом, Telegram-fallback. Не входит в KMP-таргеты клиентов. Настройки
  (`TELEGRAM_BOT_TOKEN`/`TELEGRAM_CHAT_ID`) — только через переменные окружения/`server/.env`
  (в `.gitignore`, см. `server/.env.example`), никогда не в исходниках.
- `core-ble` — работа с BLE (сканирование, GATT-подключение/подписка) через Kable. **Мёртвый
  код**: не участвует в потоке данных приложения (заменён на backend-симуляцию через
  `core-notification`), оставлен в репозитории как есть.

`core-ble`/`core-alert`/`core-notification` не знают про Android/iOS приложения — только
`sharedLogic` объединяет их в use case'ы.

Демон на Asus Tinker Board (`tinkerboard-daemon/`) — отдельный Python-проект (BlueZ/`bless`),
не часть Gradle-сборки; эмулировал BLE-периферию с датчиком движения. Не используется с переходом
на backend-симуляцию Wi-Fi Sensing — оставлен как референс, не поддерживается.

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
