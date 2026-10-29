import { cookies } from "next/headers";

// httpOnly cookie holding the backend session token. Only the Next.js server reads it
// and forwards it to the API as a Bearer token; browser JS never sees it.
export const SESSION_COOKIE = "plarent_session";

// Matches the backend's session lifetime.
const THIRTY_DAYS_IN_SECONDS = 60 * 60 * 24 * 30;

// Secure in production, unless explicitly disabled for a plain-http deployment.
const SECURE_COOKIE = process.env.NODE_ENV === "production" && process.env.SESSION_COOKIE_SECURE !== "false";

export async function getSessionToken(): Promise<string | undefined> {
  return (await cookies()).get(SESSION_COOKIE)?.value;
}

export async function setSessionCookie(token: string) {
  (await cookies()).set(SESSION_COOKIE, token, {
    httpOnly: true,
    secure: SECURE_COOKIE,
    sameSite: "lax",
    path: "/",
    maxAge: THIRTY_DAYS_IN_SECONDS,
  });
}

export async function clearSessionCookie() {
  (await cookies()).delete(SESSION_COOKIE);
}
