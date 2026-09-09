# kmp-ble-alert

Kotlin Multiplatform (Android + iOS) сервис удалённого мониторинга: критичные события
("проснулся", "долго в ванной", "не вернулся домой", SOS и т.д.) симулируются на бэкенде
(технология Wi-Fi Sensing эмулируется, без физического датчика) и доставляются клиентам по
WebSocket с подтверждением получения (ACK); при недоставке за N секунд сервер дублирует сигнал
в Telegram-бота. Подробности архитектуры — в [CLAUDE.md](./CLAUDE.md).

Модули:

- [`server`](./server) — Ktor-бэкенд: симулятор событий, WebSocket-рассылка с ACK, Telegram-fallback.
- [`core-domain`](./core-domain) — доменная модель алертов, общий wire-контракт сервера и клиентов,
  интерфейсы (`AlertRepository`, `AlertNotifier`, `NotificationPermissionManager`).
- [`core-data`](./core-data) — реализация: WebSocket-клиент, Koin-модули для DI.
- [`core-ui`](./core-ui) — общие Compose-компоненты для нескольких экранов (тема, карточки алертов и т.д.).
- [`feature-dashboard`](./feature-dashboard), [`feature-monitoring`](./feature-monitoring),
  [`feature-settings`](./feature-settings) — по одному экрану на модуль, каждый со своей ViewModel.
- [`sharedUI`](./sharedUI) — composition root: навигация (Navigation 3), Koin bootstrap,
  единственный модуль, который iOS импортирует напрямую.
- [`androidApp`](./androidApp) — Android-приложение.
- [`iosApp`](./iosApp) — Xcode-проект, тонкая обвязка над Compose Multiplatform UI.

## Запуск

Порядок важен: сначала бэкенд, потом клиент — иначе клиент просто останется в состоянии
`DISCONNECTED` (переподключение автоматическое, с backoff).

### 1. Бэкенд (`server`)

```bash
./gradlew :server:run
```

Telegram-fallback настраивается через переменные окружения (см. `server/.env.example`) —
подхватываются Gradle-таском `:server:run` автоматически из `server/.env`, который в
`.gitignore` и никогда не коммитится.

Сервер поднимается на `0.0.0.0:8080`. Полезные проверки без мобильного приложения:

```bash
# Подключиться к потоку алертов (например, websocat)
websocat ws://localhost:8080/ws/alerts

# Вручную вызвать конкретный сценарий (см. AlertType в core-domain/.../Alert.kt)
curl -X POST http://localhost:8080/simulate/sos
curl -X POST http://localhost:8080/simulate/woke_up
curl -X POST http://localhost:8080/simulate/long_bathroom_time
curl -X POST http://localhost:8080/simulate/not_returned_home
curl -X POST http://localhost:8080/simulate/no_activity
curl -X POST http://localhost:8080/simulate/device_offline
curl -X POST http://localhost:8080/simulate/motion
```

Без ручного триггера сервер сам рассылает случайный сценарий каждые 30–90 секунд. Если ни один
клиент не подтвердил алерт (ACK) за 8 секунд — или клиентов нет вовсе — уходит fallback в
Telegram.

### 2. Клиенты

Адрес бэкенда захардкожен в `core-data/src/androidMain`/`core-data/src/iosMain`
(`BackendConfig.android.kt`/`BackendConfig.ios.kt`) — под конкретное окружение тестирования его
нужно менять руками:

| Клиент | Адрес по умолчанию | Когда менять |
|---|---|---|
| Android-эмулятор | `ws://10.0.2.2:8080/ws/alerts` | не нужно (loopback на хост из коробки) |
| iOS-симулятор | `ws://localhost:8080/ws/alerts` | не нужно (симулятор шарит сеть хоста) |
| Реальное устройство | — | указать LAN IP хоста или `ngrok`-туннель |

Разные значения по умолчанию — не случайность, а два разных способа адресации хоста
(`10.0.2.2` — специальный алиас хоста внутри NAT-сети Android-эмулятора, `localhost` — потому
что iOS-симулятор физически работает в сети хоста, а не в отдельной ВМ). Сервер слушает
`0.0.0.0:8080` (см. `server/src/main/kotlin/.../Application.kt`), поэтому один и тот же LAN IP
хоста (`ipconfig getifaddr en0` на macOS) тоже подходит одновременно для эмулятора, симулятора
и реального устройства в одной Wi-Fi-сети — если нужно гонять оба клиента против одного и того
же адреса без переключения конфигурации, впишите его в оба `BackendConfig.*.kt` вместо
`10.0.2.2`/`localhost`. Недостаток такого подхода — IP меняется при смене сети, поэтому
платформенные алиасы остаются вариантом по умолчанию, не требующим ручной настройки.

**Android:**

```bash
./gradlew :androidApp:installDevDebug
```

или через run-конфигурацию IDE (`androidApp`, флейвор `dev`).

**iOS:** открыть [`iosApp/iosApp.xcodeproj`](./iosApp/iosApp.xcodeproj) в Xcode и запустить на
симуляторе/устройстве. Из терминала:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 17' build
```

Приложение открывается сразу на экране Dashboard и подключается к бэкенду автоматически. Кнопка "Disconnect"/"Reconnect" на Dashboard/Monitoring — для ручной
проверки сценария разрыва соединения и Telegram-fallback.

---

Подробнее про Kotlin Multiplatform: [официальная документация](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).
