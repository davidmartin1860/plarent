import Foundation

struct Plant: Codable, Identifiable {
    let id: String
    let name: String
    let species: String
    let location: String?
    let acquiredDate: String?
    let notes: String?
    let hasImage: Bool
    let createdAt: String
    let updatedAt: String
}

struct CreatePlantInput: Codable {
    let name: String
    let species: String
    let location: String?
    let acquiredDate: String?
    let notes: String?
}
