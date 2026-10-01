import SwiftUI

struct PlantDetailView: View {
    let plantService: PlantService
    let plantId: String

    @State private var plant: Plant?
    @State private var isLoading = false
    @State private var errorMessage: String?
    @State private var notFound = false

    var body: some View {
        content
            .navigationTitle(plant?.name ?? "Plant")
            .task {
                await loadPlant()
            }
    }

    @ViewBuilder
    private var content: some View {
        if isLoading {
            ProgressView("Loading…")
        } else if let errorMessage {
            VStack(spacing: 12) {
                Text("Couldn't load plant")
                    .font(.headline)
                Text(errorMessage)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
                Button("Retry") {
                    Task { await loadPlant() }
                }
            }
            .padding()
        } else if notFound {
            ContentUnavailableView(
                "Plant Not Found",
                systemImage: "questionmark.folder",
                description: Text("This plant may have been removed.")
            )
        } else if let plant {
            Form {
                Section("Details") {
                    LabeledContent("Name", value: plant.name)
                    if let species = plant.species, !species.isEmpty {
                        LabeledContent("Species", value: species)
                    }
                    if let location = plant.location, !location.isEmpty {
                        LabeledContent("Location", value: location)
                    }
                    if let acquiredDate = plant.acquiredDate, !acquiredDate.isEmpty {
                        LabeledContent("Acquired", value: acquiredDate)
                    }
                }
                if let notes = plant.notes, !notes.isEmpty {
                    Section("Notes") {
                        Text(notes)
                    }
                }
                Section {
                    LabeledContent("Created", value: plant.createdAt)
                    LabeledContent("Updated", value: plant.updatedAt)
                }
                .font(.caption)
                .foregroundStyle(.secondary)
            }
        }
    }

    private func loadPlant() async {
        isLoading = true
        errorMessage = nil
        notFound = false
        do {
            if let result = try await plantService.getPlant(id: plantId) {
                plant = result
            } else {
                notFound = true
            }
        } catch {
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}

#Preview {
    NavigationStack {
        PlantDetailView(plantService: PlantService(), plantId: "preview-id")
    }
}
