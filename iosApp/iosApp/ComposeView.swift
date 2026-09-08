import SwiftUI
import SharedUI

struct ComposeView: UIViewControllerRepresentable {
    let viewModel: MonitoringViewModel

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(viewModel: viewModel)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
