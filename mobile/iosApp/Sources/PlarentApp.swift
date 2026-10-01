import SwiftUI

@main
struct PlarentApp: App {
    private let plantService = PlantService()

    var body: some Scene {
        WindowGroup {
            NavigationStack {
                PlantListView(plantService: plantService)
            }
        }
    }
}
