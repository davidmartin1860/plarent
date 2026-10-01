import SwiftUI

struct PlantListView: View {
    let plantService: PlantService

    @State private var plants: [Plant] = []
    @State private var isLoading = false
    @State private var errorMessage: String?

    var body: some View {
        content
            .navigationTitle("Plants")
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    NavigationLink {
                        AddPlantView(plantService: plantService, onSaved: { newPlant in
                            plants.append(newPlant)
                        })
                    } label: {
                        Image(systemName: "plus")
                    }
                }
            }
            .task {
                await loadPlants()
            }
    }

    @ViewBuilder
    private var content: some View {
        if isLoading && plants.isEmpty {
            ProgressView("Loading plants…")
        } else if let errorMessage {
            VStack(spacing: 12) {
                Text("Couldn't load plants")
                    .font(.headline)
                Text(errorMessage)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
                Button("Retry") {
                    Task { await loadPlants() }
                }
            }
            .padding()
        } else if plants.isEmpty {
            ContentUnavailableView(
                "No Plants Yet",
                systemImage: "leaf",
                description: Text("Tap + to add your first plant.")
            )
        } else {
            List(plants) { plant in
                NavigationLink {
                    PlantDetailView(plantService: plantService, plantId: plant.id)
                } label: {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(plant.name)
                            .font(.headline)
                        Text(plant.species)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                        if let location = plant.location, !location.isEmpty {
                            Text(location)
                                .font(.caption)
                                .foregroundStyle(.tertiary)
                        }
                    }
                }
            }
            .refreshable {
                await loadPlants()
            }
        }
    }

    private func loadPlants() async {
        isLoading = true
        errorMessage = nil
        do {
            plants = try await plantService.listPlants()
        } catch {
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}

#Preview {
    NavigationStack {
        PlantListView(plantService: PlantService())
    }
}
