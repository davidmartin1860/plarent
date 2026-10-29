"use server";

import { redirect } from "next/navigation";
import { createPlant, deletePlant, uploadPlantImage } from "@/lib/api";
import { imageFromForm, parsePlantForm } from "../plant-form-data";

export async function createPlantAction(formData: FormData): Promise<void> {
  const plant = await createPlant(parsePlantForm(formData));
  const image = imageFromForm(formData);
  if (image) {
    try {
      await uploadPlantImage(plant.id, image);
    } catch (error) {
      try {
        await deletePlant(plant.id);
      } catch {
        // Keep the upload error; the plant stays without a photo if cleanup fails.
      }
      throw error;
    }
  }

  redirect(`/plants/${plant.id}`);
}
