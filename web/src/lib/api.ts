import type { PlantInput, Plant } from "./types";

const API_URL = process.env.API_URL ?? "http://localhost:8080/api";

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
  ) {
    super(message);
  }
}

export async function getPlants(): Promise<Plant[]> {
  const res = await fetch(`${API_URL}/plants`, { cache: "no-store" });
  if (!res.ok) {
    throw new ApiError(`Failed to load plants (${res.status})`, res.status);
  }
  return res.json();
}

export async function getPlant(id: string): Promise<Plant | null> {
  const res = await fetch(`${API_URL}/plants/${id}`, { cache: "no-store" });
  if (res.status === 404) {
    return null;
  }
  if (!res.ok) {
    throw new ApiError(`Failed to load plant ${id} (${res.status})`, res.status);
  }
  return res.json();
}

export async function createPlant(input: PlantInput): Promise<Plant> {
  const res = await fetch(`${API_URL}/plants`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  });
  if (!res.ok) {
    throw new ApiError(`Failed to create plant (${res.status})`, res.status);
  }
  return res.json();
}

export async function updatePlant(id: string, input: Partial<PlantInput>): Promise<Plant> {
  const res = await fetch(`${API_URL}/plants/${id}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  });
  if (!res.ok) {
    throw new ApiError(`Failed to update plant ${id} (${res.status})`, res.status);
  }
  return res.json();
}

export async function uploadPlantImage(id: string, file: File): Promise<Plant> {
  const body = new FormData();
  body.append("image", file, file.name || "plant");
  const res = await fetch(`${API_URL}/plants/${id}/image`, { method: "POST", body });
  if (!res.ok) {
    throw new ApiError(await apiErrorMessage(res, `Failed to upload image for plant ${id}`), res.status);
  }
  return res.json();
}

export async function fetchPlantImage(id: string): Promise<Response> {
  return fetch(`${API_URL}/plants/${id}/image`, { cache: "no-store" });
}

async function apiErrorMessage(res: Response, fallback: string): Promise<string> {
  try {
    const body = (await res.json()) as { message?: string };
    if (body.message) {
      return body.message;
    }
  } catch {
    // The body is not JSON.
  }
  return `${fallback} (${res.status})`;
}

export async function deletePlant(id: string): Promise<void> {
  const res = await fetch(`${API_URL}/plants/${id}`, { method: "DELETE" });
  if (!res.ok) {
    throw new ApiError(`Failed to delete plant ${id} (${res.status})`, res.status);
  }
}
