import SwiftUI
import SharedLogic

struct ContentView: View {
    @StateObject private var observable: MonitoringObservable

    init(appContainer: AppContainer) {
        _observable = StateObject(wrappedValue: MonitoringObservable(viewModel: appContainer.createMonitoringViewModel()))
    }

    var body: some View {
        NavigationStack {
            Group {
                switch observable.connectionState {
                case .connected, .connecting:
                    monitoringView
                case .idle, .scanning, .disconnected:
                    deviceListView
                }
            }
            .navigationTitle("BLE Alert")
        }
        .task { await observable.start() }
    }

    private var deviceListView: some View {
        List(observable.devices, id: \.id) { device in
            Button {
                observable.connect(device)
            } label: {
                VStack(alignment: .leading) {
                    Text(device.name ?? "Unknown device")
                    Text(device.id).font(.caption).foregroundColor(.secondary)
                }
            }
        }
    }

    private var monitoringView: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Text(String(describing: observable.connectionState))
                    .font(.headline)
                Spacer()
                Button("Disconnect") { observable.disconnect() }
            }
            .padding()

            if observable.alerts.isEmpty {
                Spacer()
                Text("No alerts yet")
                    .foregroundColor(.secondary)
                Spacer()
            } else {
                List(observable.alerts.reversed(), id: \.id) { alert in
                    HStack {
                        Text(String(describing: alert.type))
                        Spacer()
                        Text(String(describing: alert.severity))
                            .font(.caption)
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(Color.red.opacity(0.2))
                            .clipShape(Capsule())
                    }
                }
            }
        }
    }
}
