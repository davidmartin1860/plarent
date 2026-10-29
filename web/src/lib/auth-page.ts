import { redirect } from "next/navigation";
import { getMe } from "./api";
import { getSessionToken } from "./session";

// Login/register pages send already signed-in users home. getMe() redirects to
// /session-expired on a stale token, which clears the cookie and comes back here.
export async function redirectIfSignedIn() {
  if (await getSessionToken()) {
    await getMe();
    redirect("/");
  }
}
