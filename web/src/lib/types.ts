export interface Plant {
  id: string;
  name: string;
  species: string | null;
  location: string | null;
  acquiredDate: string | null;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePlantInput {
  name: string;
  species?: string;
  location?: string;
  acquiredDate?: string;
  notes?: string;
}
