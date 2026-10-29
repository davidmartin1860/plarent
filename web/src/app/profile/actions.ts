"use server";

import { revalidatePath } from "next/cache";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { logout } from "@/lib/api";
import { isLocale, LOCALE_COOKIE } from "@/lib/i18n";
import { clearSessionCookie, getSessionToken } from "@/lib/session";

const ONE_YEAR_IN_SECONDS = 60 * 60 * 24 * 365;

export async function setLocaleAction(locale: string) {
  if (!isLocale(locale)) {
    throw new Error(`Unsupported locale "${locale}"`);
  }

  (await cookies()).set(LOCALE_COOKIE, locale, {
    path: "/",
    maxAge: ONE_YEAR_IN_SECONDS,
    sameSite: "lax",
  });
  revalidatePath("/", "layout");
}

export async function logoutAction() {
  const token = await getSessionToken();
  if (token) {
    await logout(token);
  }
  await clearSessionCookie();
  redirect("/login");
}
