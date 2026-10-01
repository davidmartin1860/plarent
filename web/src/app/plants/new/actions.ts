"use server";

import { redirect } from "next/navigation";
import { createPlant } from "@/lib/api";

export async function createPlantAction(formData: FormData): Promise<void> {
  const name = String(formData.get("name") ?? "").trim();
  if (!name) {
    throw new Error("Name is required");
  }

  const species = String(formData.get("species") ?? "").trim();
  if (!species) {
    throw new Error("Species is required");
  }

  const optional = (field: string): string | undefined => {
    const value = String(formData.get(field) ?? "").trim();
    return value.length > 0 ? value : undefined;
  };

  const plant = await createPlant({
    name,
    species,
    location: optional("location"),
    acquiredDate: optional("acquiredDate"),
    notes: optional("notes"),
  });

  redirect(`/plants/${plant.id}`);
}
