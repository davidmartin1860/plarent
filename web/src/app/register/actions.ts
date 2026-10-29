"use server";

import { redirect } from "next/navigation";
import type { AuthFormState } from "@/components/auth-form";
import { register } from "@/lib/api";
import { setSessionCookie } from "@/lib/session";

export async function registerAction(_state: AuthFormState, formData: FormData): Promise<AuthFormState> {
  const username = String(formData.get("username") ?? "");
  const result = await register({ username, password: String(formData.get("password") ?? "") });

  if (!result.ok) {
    if (result.status === 409) return { error: "auth.errors.usernameTaken", username };
    if (result.status === 400) return { error: "auth.errors.invalidInput", username };
    return { error: "auth.errors.unexpected", username };
  }

  await setSessionCookie(result.token);
  redirect("/");
}
