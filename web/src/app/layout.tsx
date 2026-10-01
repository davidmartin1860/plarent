import type { Metadata } from "next";
import Link from "next/link";
import { t } from "@/lib/i18n";
import "./globals.css";

export const metadata: Metadata = {
  title: t("common.appName"),
  description: t("common.appTagline"),
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <div className="min-h-screen">
          <header className="border-b border-stone-200 bg-white">
            <div className="mx-auto flex max-w-3xl items-center justify-between px-4 py-4">
              <Link href="/" className="text-lg font-semibold text-emerald-800">
                🌱 {t("common.appName")}
              </Link>
              <Link
                href="/plants/new"
                className="rounded-md bg-emerald-700 px-3 py-1.5 text-sm font-medium text-white hover:bg-emerald-800"
              >
                {t("common.addPlant")}
              </Link>
            </div>
          </header>
          <main className="mx-auto max-w-3xl px-4 py-8">{children}</main>
        </div>
      </body>
    </html>
  );
}
