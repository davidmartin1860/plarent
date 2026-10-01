import type { Metadata } from "next";
import Link from "next/link";
import { AppFooter } from "@/components/app-footer";
import { t } from "@/lib/i18n";
import { ThemeToggle } from "@/components/theme-toggle";
import "./globals.css";

export const metadata: Metadata = {
  title: t("common.appName"),
  description: t("common.appTagline"),
};

const themeInitScript = `
(function () {
  try {
    var stored = localStorage.getItem("theme");
    var isDark = stored === "dark" || (stored !== "light" && window.matchMedia("(prefers-color-scheme: dark)").matches);
    document.documentElement.classList.toggle("dark", isDark);
  } catch (e) {}
})();
`;

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <script dangerouslySetInnerHTML={{ __html: themeInitScript }} />
      </head>
      <body>
        <div className="min-h-screen pb-24">
          <header className="border-b border-stone-200 bg-white dark:border-stone-800 dark:bg-stone-900">
            <div className="mx-auto flex max-w-3xl items-center justify-between px-4 py-4">
              <Link href="/" className="text-lg font-semibold text-emerald-800 dark:text-emerald-400">
                🌱 {t("common.appName")}
              </Link>
              <div className="flex items-center gap-2">
                <Link
                  href="/plants/new"
                  className="rounded-md bg-emerald-700 px-3 py-1.5 text-sm font-medium text-white hover:bg-emerald-800 dark:bg-emerald-600 dark:hover:bg-emerald-700"
                >
                  {t("common.addPlant")}
                </Link>
                <ThemeToggle />
              </div>
            </div>
          </header>
          <main className="mx-auto max-w-3xl px-4 py-8">{children}</main>
          <AppFooter />
        </div>
      </body>
    </html>
  );
}
