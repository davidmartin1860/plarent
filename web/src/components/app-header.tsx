"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { ThemeToggle } from "@/components/theme-toggle";
import { t } from "@/lib/i18n";

export function AppHeader() {
  const pathname = usePathname();

  if (pathname === "/") {
    return null;
  }

  return (
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
  );
}
