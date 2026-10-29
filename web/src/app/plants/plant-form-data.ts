import type { PlantInput } from "@/lib/types";

export function parsePlantForm(formData: FormData): PlantInput {
  const name = String(formData.get("name") ?? "").trim();
  if (!name) {
    throw new Error("Name is required");
  }

  const species = String(formData.get("species") ?? "").trim();
  if (!species) {
    throw new Error("Species is required");
  }

  // Fields the form does not render stay undefined, so an update leaves them unchanged.
  // A field left empty is sent as "", which the API stores as null.
  const optional = (field: string): string | undefined => {
    const value = formData.get(field);
    return value === null ? undefined : String(value).trim();
  };

  return {
    name,
    species,
    location: optional("location"),
    acquiredDate: optional("acquiredDate"),
    notes: optional("notes"),
  };
}

export function imageFromForm(formData: FormData): File | null {
  const value = formData.get("image");
  if (!(value instanceof File) || value.size === 0) {
    return null;
  }
  return value;
}

export function plantImageUrl(plant: { id: string; updatedAt: string; hasImage: boolean }): string | null {
  if (!plant.hasImage) {
    return null;
  }
  return `/plants/${plant.id}/image?v=${encodeURIComponent(plant.updatedAt)}`;
}
