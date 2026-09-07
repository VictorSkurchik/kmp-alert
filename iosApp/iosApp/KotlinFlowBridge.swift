import Foundation
import SharedLogic

/// SKIE bridges suspend functions to async/await automatically, but (in this SKIE/Kotlin
/// version combination) leaves `Flow`/`StateFlow`-typed *properties* as the raw Kotlin
/// coroutines-core protocol rather than an `AsyncSequence`. This adapter bridges any Kotlin
/// `Flow` to a native `AsyncStream` using the one piece SKIE does expose by default: the
/// `collect(collector:)` suspend function.
private final class FlowCollectorBridge: NSObject, Kotlinx_coroutines_coreFlowCollector {
    private let onEmit: (Any?) -> Void

    init(onEmit: @escaping (Any?) -> Void) {
        self.onEmit = onEmit
    }

    func emit(value: Any?, completionHandler: @escaping (Error?) -> Void) {
        onEmit(value)
        completionHandler(nil)
    }
}

extension Kotlinx_coroutines_coreFlow {
    func asAsyncStream<T>(of type: T.Type) -> AsyncStream<T> {
        AsyncStream { continuation in
            let collector = FlowCollectorBridge { value in
                if let typed = value as? T {
                    continuation.yield(typed)
                }
            }
            let task = Task {
                _ = try? await self.collect(collector: collector)
                continuation.finish()
            }
            continuation.onTermination = { _ in task.cancel() }
        }
    }
}
