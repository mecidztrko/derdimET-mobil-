import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}

private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(apiBaseUrl: "http://localhost:8081")
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
