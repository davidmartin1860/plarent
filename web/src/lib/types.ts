export interface Plant {
  id: string;
  name: string;
  species: string;
  location: string | null;
  acquiredDate: string | null;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PlantInput {
  name: string;
  species: string;
  location?: string;
  acquiredDate?: string;
  notes?: string;
}

export interface User {
  id: string;
  username: string;
  createdAt: string;
}

export interface AuthCredentials {
  username: string;
  password: string;
}
