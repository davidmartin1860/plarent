export interface Plant {
  id: string;
  name: string;
  species: string;
  location: string | null;
  acquiredDate: string | null;
  notes: string | null;
  hasImage: boolean;
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
