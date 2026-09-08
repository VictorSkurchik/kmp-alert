package by.vsdev.blealert.data

/**
 * Where `:server`'s WebSocket endpoint is reachable from this platform's test environment.
 * Android emulator loopback vs. iOS simulator localhost vs. a real device's LAN IP/tunnel differ,
 * so this is intentionally not auto-detected - update the platform actual for your environment.
 */
internal expect val backendUrl: String
