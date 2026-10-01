import type { CreatePlantInput, Plant } from "./types";

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

export async function createPlant(input: CreatePlantInput): Promise<Plant> {
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
