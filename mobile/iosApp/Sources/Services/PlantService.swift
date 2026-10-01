import Foundation

enum PlantServiceError: Error, LocalizedError {
    case invalidResponse
    case server(statusCode: Int, body: String?)

    var errorDescription: String? {
        switch self {
        case .invalidResponse:
            return "The server returned an unexpected response."
        case .server(let statusCode, let body):
            if let body, !body.isEmpty {
                return "Server error (\(statusCode)): \(body)"
            }
            return "Server error (\(statusCode))."
        }
    }
}

actor PlantService {
    // The iOS Simulator shares the host Mac's network namespace, so `localhost`
    // reaches a backend running on the host directly (unlike the Android emulator,
    // which needs 10.0.2.2 to reach the host).
    private let baseURL: URL
    private let session: URLSession
    private let encoder: JSONEncoder
    private let decoder: JSONDecoder

    init(baseURL: URL = URL(string: "http://localhost:8080/api")!, session: URLSession = .shared) {
        self.baseURL = baseURL
        self.session = session

        let encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .useDefaultKeys
        self.encoder = encoder

        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .useDefaultKeys
        self.decoder = decoder
    }

    func listPlants() async throws -> [Plant] {
        let url = baseURL.appendingPathComponent("plants")
        let (data, response) = try await session.data(from: url)
        try validate(response: response, data: data, allowing: [200])
        return try decoder.decode([Plant].self, from: data)
    }

    func getPlant(id: String) async throws -> Plant? {
        let url = baseURL.appendingPathComponent("plants").appendingPathComponent(id)
        let (data, response) = try await session.data(from: url)

        guard let httpResponse = response as? HTTPURLResponse else {
            throw PlantServiceError.invalidResponse
        }
        if httpResponse.statusCode == 404 {
            return nil
        }
        try validate(response: response, data: data, allowing: [200])
        return try decoder.decode(Plant.self, from: data)
    }

    func createPlant(_ input: CreatePlantInput) async throws -> Plant {
        let url = baseURL.appendingPathComponent("plants")
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try encoder.encode(input)

        let (data, response) = try await session.data(for: request)
        try validate(response: response, data: data, allowing: [201])
        return try decoder.decode(Plant.self, from: data)
    }

    private func validate(response: URLResponse, data: Data, allowing okCodes: Set<Int>) throws {
        guard let httpResponse = response as? HTTPURLResponse else {
            throw PlantServiceError.invalidResponse
        }
        guard okCodes.contains(httpResponse.statusCode) else {
            let body = String(data: data, encoding: .utf8)
            throw PlantServiceError.server(statusCode: httpResponse.statusCode, body: body)
        }
    }
}
