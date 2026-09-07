# CLAUDE.md

## Архитектура

Kotlin Multiplatform + Compose Multiplatform, Clean Architecture, модульная сборка, SOLID.
Android UI использует atomic design (atoms/molecules/organisms/templates/screens в `sharedUI`).
iOS использует нативный SwiftUI поверх общей бизнес-логики (не Compose UI на iOS), с SKIE для
идиоматичного вызова Kotlin suspend-функций и `Flow` из Swift (`async`/`await`, `AsyncSequence`).

Модули:
- `androidApp` — Android-приложение: DI-обвязка, разрешения, манифест, foreground-сервис.
- `iosApp` — Xcode-проект, нативный SwiftUI, линкует фреймворк `SharedLogic`.
- `sharedLogic` — доменный/оркестрирующий слой, единственный модуль, который собирает и
  экспортирует iOS-фреймворк (реэкспортирует публичные API `core-ble` и `core-alert`).
- `sharedUI` — Compose Multiplatform UI для Android (atomic design).
- `core-ble` — работа с BLE (сканирование, GATT-подключение/подписка) через Kable.
- `core-alert` — модуль Alert Notification: доменная модель алертов, разбор payload, платформенные уведомления.

`core-ble` и `core-alert` не зависят друг от друга и не знают про Android/iOS приложения —
только `sharedLogic` объединяет их в use case'ы.

Демон на Asus Tinker Board (`tinkerboard-daemon/`) — отдельный Python-проект (BlueZ/`bless`),
не часть Gradle-сборки: эмулирует BLE-периферию с датчиком движения и шлёт alert'ы клиентам.

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
