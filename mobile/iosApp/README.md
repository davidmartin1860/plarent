# Plarent iOS App

A standalone SwiftUI app (iOS 17+) that talks directly to the Plarent backend's HTTP JSON API
via `URLSession`. It does not depend on the Kotlin Multiplatform `mobile/shared` module — all
networking and models are native Swift.

This directory contains an [XcodeGen](https://github.com/yonaskolb/XcodeGen) `project.yml`
spec instead of a committed `.xcodeproj`. XcodeGen specs are plain, readable YAML that generate
a real Xcode project on demand, which avoids checking in an opaque, merge-hostile
`project.pbxproj` file.

## Prerequisites

- Xcode (full install, not just Command Line Tools), with an iOS 17 Simulator runtime
- [Homebrew](https://brew.sh)

## Setup

```sh
brew install xcodegen
cd mobile/iosApp
xcodegen generate
open Plarent.xcodeproj
```

Then pick an iOS Simulator and hit Run in Xcode.

## Backend connectivity

The app's `PlantService` defaults to `http://localhost:8080/api`. Start the backend on your
host Mac (see `backend/` and `compose.yaml` at the repo root) before running the app.

The iOS Simulator shares the host Mac's network namespace, so `localhost` reaches a
backend running on the host directly. This is different from the Android emulator, which
needs `10.0.2.2` to reach the host machine — if you're also working on `mobile/androidApp`,
don't copy its base URL here.

Because this points at a plain `http://` (non-TLS) localhost address, `project.yml` adds an
App Transport Security exception scoped specifically to the `localhost` domain (not a blanket
ATS disable) so the Simulator will allow the request.

## Project layout

```
mobile/iosApp/
  project.yml                  # XcodeGen spec (source of truth for project structure)
  Sources/
    PlarentApp.swift            # @main App entry point
    Models/Plant.swift          # Codable models matching the backend JSON contract
    Services/PlantService.swift # URLSession-based API client
    Views/
      PlantListView.swift
      PlantDetailView.swift
      AddPlantView.swift
```

Running `xcodegen generate` produces `Plarent.xcodeproj` (git-ignored) from this spec.
