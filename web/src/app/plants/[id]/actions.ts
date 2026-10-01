"use server";

import { redirect } from "next/navigation";
import { deletePlant } from "@/lib/api";

export async function deletePlantAction(id: string): Promise<void> {
  await deletePlant(id);
  redirect("/");
}
