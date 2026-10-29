import { redirect } from "next/navigation";
import { getSessionToken } from "./session";
import type { AuthCredentials, PlantInput, Plant, User } from "./types";

const API_URL = process.env.API_URL ?? "http://localhost:8080/api";

// Route handler that clears a stale session cookie and sends the user to /login.
// Server Components can't modify cookies themselves, so a 401 redirects there.
export const SESSION_EXPIRED_PATH = "/session-expired";

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
  ) {
    super(message);
  }
}

// Authenticated request: sends the session token and treats a 401 as an expired session.
async function apiFetch(path: string, init: RequestInit = {}): Promise<Response> {
  const token = await getSessionToken();
  const headers = new Headers(init.headers);
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  const res = await fetch(`${API_URL}${path}`, { cache: "no-store", ...init, headers });
  if (res.status === 401) {
    redirect(SESSION_EXPIRED_PATH);
  }
  return res;
}

export async function getPlants(): Promise<Plant[]> {
  const res = await apiFetch("/plants");
  if (!res.ok) {
    throw new ApiError(`Failed to load plants (${res.status})`, res.status);
  }
  return res.json();
}

export async function getPlant(id: string): Promise<Plant | null> {
  const res = await apiFetch(`/plants/${id}`);
  if (res.status === 404) {
    return null;
  }
  if (!res.ok) {
    throw new ApiError(`Failed to load plant ${id} (${res.status})`, res.status);
  }
  return res.json();
}

export async function createPlant(input: PlantInput): Promise<Plant> {
  const res = await apiFetch("/plants", {
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
  const res = await apiFetch(`/plants/${id}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  });
  if (!res.ok) {
    throw new ApiError(`Failed to update plant ${id} (${res.status})`, res.status);
  }
  return res.json();
}

export async function deletePlant(id: string): Promise<void> {
  const res = await apiFetch(`/plants/${id}`, { method: "DELETE" });
  if (!res.ok) {
    throw new ApiError(`Failed to delete plant ${id} (${res.status})`, res.status);
  }
}

export async function getMe(): Promise<User> {
  const res = await apiFetch("/me");
  if (!res.ok) {
    throw new ApiError(`Failed to load user (${res.status})`, res.status);
  }
  return res.json();
}

export type AuthResult = { ok: true; token: string; user: User } | { ok: false; status: number };

// Login/register failures (bad credentials, taken username, validation) are expected
// outcomes the form displays, so they're returned rather than thrown.
async function authenticate(path: string, credentials: AuthCredentials): Promise<AuthResult> {
  const res = await fetch(`${API_URL}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(credentials),
    cache: "no-store",
  });
  if (!res.ok) {
    return { ok: false, status: res.status };
  }
  const { token, user } = await res.json();
  return { ok: true, token, user };
}

export function login(credentials: AuthCredentials): Promise<AuthResult> {
  return authenticate("/auth/login", credentials);
}

export function register(credentials: AuthCredentials): Promise<AuthResult> {
  return authenticate("/auth/register", credentials);
}

export async function logout(token: string): Promise<void> {
  await fetch(`${API_URL}/auth/logout`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
    cache: "no-store",
  });
}
