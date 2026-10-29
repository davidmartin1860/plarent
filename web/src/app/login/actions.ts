"use server";

import { redirect } from "next/navigation";
import type { AuthFormState } from "@/components/auth-form";
import { login } from "@/lib/api";
import { setSessionCookie } from "@/lib/session";

export async function loginAction(_state: AuthFormState, formData: FormData): Promise<AuthFormState> {
  const username = String(formData.get("username") ?? "");
  const result = await login({ username, password: String(formData.get("password") ?? "") });

  if (!result.ok) {
    return {
      error: result.status === 429 ? "auth.errors.tooManyAttempts" : "auth.errors.invalidCredentials",
      username,
    };
  }

  await setSessionCookie(result.token);
  redirect("/");
}
