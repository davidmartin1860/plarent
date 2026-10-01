import SwiftUI

struct AddPlantView: View {
    let plantService: PlantService
    var onSaved: (Plant) -> Void = { _ in }

    @Environment(\.dismiss) private var dismiss

    @State private var name = ""
    @State private var species = ""
    @State private var location = ""
    @State private var acquiredDate = ""
    @State private var notes = ""

    @State private var isSaving = false
    @State private var errorMessage: String?

    private var trimmedName: String {
        name.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private var isValid: Bool {
        !trimmedName.isEmpty && trimmedName.count <= 100
    }

    var body: some View {
        Form {
            Section("Plant") {
                TextField("Name", text: $name)
                TextField("Species", text: $species)
                TextField("Location", text: $location)
                TextField("Acquired Date (YYYY-MM-DD)", text: $acquiredDate)
                    .autocorrectionDisabled()
                    .textInputAutocapitalization(.never)
            }
            Section("Notes") {
                TextField("Notes", text: $notes, axis: .vertical)
                    .lineLimit(4...8)
            }
            if let errorMessage {
                Section {
                    Text(errorMessage)
                        .foregroundStyle(.red)
                        .font(.footnote)
                }
            }
        }
        .navigationTitle("Add Plant")
        .toolbar {
            ToolbarItem(placement: .confirmationAction) {
                if isSaving {
                    ProgressView()
                } else {
                    Button("Save") {
                        save()
                    }
                    .disabled(!isValid)
                }
            }
        }
    }

    private func save() {
        errorMessage = nil
        isSaving = true

        let input = CreatePlantInput(
            name: trimmedName,
            species: nonEmpty(species),
            location: nonEmpty(location),
            acquiredDate: nonEmpty(acquiredDate),
            notes: nonEmpty(notes)
        )

        Task {
            do {
                let created = try await plantService.createPlant(input)
                isSaving = false
                onSaved(created)
                dismiss()
            } catch {
                isSaving = false
                errorMessage = error.localizedDescription
            }
        }
    }

    private func nonEmpty(_ value: String) -> String? {
        let trimmed = value.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.isEmpty ? nil : trimmed
    }
}

#Preview {
    NavigationStack {
        AddPlantView(plantService: PlantService())
    }
}
