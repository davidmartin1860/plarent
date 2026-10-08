import type { Metadata } from "next";
import { Inter, Playfair_Display, Quicksand } from "next/font/google";
import Link from "next/link";
import { AppFooter } from "@/components/app-footer";
import { t } from "@/lib/i18n";
import { ThemeToggle } from "@/components/theme-toggle";
import "./globals.css";

export const metadata: Metadata = {
  title: t("common.appName"),
  description: t("common.appTagline"),
};

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
  fallback: ["ui-sans-serif", "system-ui", "Segoe UI"],
});
const quicksand = Quicksand({
  subsets: ["latin"],
  variable: "--font-quicksand",
  fallback: ["Trebuchet MS", "ui-sans-serif", "system-ui"],
});
const playfairDisplay = Playfair_Display({
  subsets: ["latin"],
  variable: "--font-playfair-display",
  fallback: ["Georgia", "Times New Roman"],
});

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
    <html
      lang="en"
      suppressHydrationWarning
      className={`${inter.variable} ${quicksand.variable} ${playfairDisplay.variable}`}
    >
      <head>
        <script dangerouslySetInnerHTML={{ __html: themeInitScript }} />
      </head>
      <body>
        <div className="min-h-screen pb-24">
          <header className="border-b border-secondary/30 bg-boxes-secondary">
            <div className="mx-auto flex max-w-3xl items-center justify-between px-page-x py-4">
              <Link href="/" className="font-display text-lg font-semibold text-primary">
                🌱 {t("common.appName")}
              </Link>
              <div className="flex items-center gap-2">
                <Link
                  href="/plants/new"
                  className="rounded-full bg-secondary-title px-3 py-1.5 font-sans text-sm font-semibold text-background hover:opacity-90"
                >
                  {t("common.addPlant")}
                </Link>
                <ThemeToggle />
              </div>
            </div>
          </header>
          <main className="mx-auto max-w-3xl px-page-x py-8">{children}</main>
          <AppFooter />
        </div>
      </body>
    </html>
  );
}
