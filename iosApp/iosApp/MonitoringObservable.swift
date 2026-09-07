import Foundation
import SharedLogic

@MainActor
final class MonitoringObservable: ObservableObject {
    @Published var connectionState: ConnectionUiState = .idle
    @Published var alerts: [Alert] = []
    @Published var devices: [BleDevice] = []

    private let viewModel: MonitoringViewModel

    init(viewModel: MonitoringViewModel) {
        self.viewModel = viewModel
    }

    func start() async {
        viewModel.startScanning()
        async let uiStateTask: Void = observeUiState()
        async let devicesTask: Void = observeDevices()
        _ = await (uiStateTask, devicesTask)
    }

    func connect(_ device: BleDevice) {
        viewModel.connect(device: device)
    }

    func disconnect() {
        viewModel.disconnect()
    }

    private func observeUiState() async {
        for await state in viewModel.uiState {
            connectionState = state.connectionState
            alerts = state.alerts
        }
    }

    private func observeDevices() async {
        for await deviceList in viewModel.devices {
            devices = deviceList
        }
    }
}
