"use server";

import { redirect } from "next/navigation";
import { deletePlant, updatePlant, uploadPlantImage } from "@/lib/api";
import { imageFromForm, parsePlantForm } from "../plant-form-data";

export async function updatePlantAction(id: string, formData: FormData): Promise<void> {
  await updatePlant(id, parsePlantForm(formData));
  const image = imageFromForm(formData);
  if (image) {
    await uploadPlantImage(id, image);
  }
  redirect("/plants");
}

export async function deletePlantAction(id: string): Promise<void> {
  await deletePlant(id);
  redirect("/plants");
}
