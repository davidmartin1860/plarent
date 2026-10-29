import { NextResponse } from "next/server";
import { clearSessionCookie } from "@/lib/session";

export async function GET() {
  await clearSessionCookie();
  // Relative on purpose: behind Docker, request.url carries the container's hostname.
  return new NextResponse(null, { status: 307, headers: { Location: "/login" } });
}
