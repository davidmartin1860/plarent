"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Logo } from "@/components/logo";
import { t, type TranslationKey } from "@/lib/i18n";

type FooterLink = {
  href: string;
  labelKey: TranslationKey;
  column: string;
};

const LEFT_LINKS: FooterLink[] = [
  { href: "/plants", labelKey: "nav.myPlants", column: "col-start-1" },
  { href: "/calendar", labelKey: "nav.taskCalendar", column: "col-start-2" },
];

const RIGHT_LINKS: FooterLink[] = [
  { href: "/points", labelKey: "nav.plaerrPoints", column: "col-start-4" },
  { href: "/profile", labelKey: "nav.myProfile", column: "col-start-5" },
];

function FooterLinkItem({ item, pathname }: { item: FooterLink; pathname: string }) {
  const label = t(item.labelKey);
  const active = isTabActive(pathname, item.href);

  return (
    <Link
      href={item.href}
      aria-current={active ? "page" : undefined}
      className={[
        "type-tab-bar mx-auto block w-[75px] text-center",
        item.column,
        active ? "text-highlight" : "text-primary",
      ].join(" ")}
    >
      {label}
    </Link>
  );
}

function isTabActive(pathname: string, href: string): boolean {
  return pathname === href || pathname.startsWith(`${href}/`);
}

export function AppFooter() {
  const pathname = usePathname();
  const isHome = pathname === "/";

  return (
    <footer className="fixed inset-x-0 bottom-0 z-40 bg-tab-bar pb-[env(safe-area-inset-bottom)] text-primary">
      <nav aria-label="Main" className="relative mx-auto grid h-[60px] max-w-3xl grid-cols-5 items-center">
        {LEFT_LINKS.map((item) => (
          <FooterLinkItem key={item.href} item={item} pathname={pathname} />
        ))}
        <Link
          href="/"
          aria-label={t("common.home")}
          aria-current={isHome ? "page" : undefined}
          className={[
            "absolute left-1/2 -translate-x-1/2",
            isHome ? "-top-3 h-[65px] w-[71px]" : "top-1.5 h-[47px] w-[51px]",
          ].join(" ")}
        >
          <Logo className="h-full w-full" />
        </Link>
        {RIGHT_LINKS.map((item) => (
          <FooterLinkItem key={item.href} item={item} pathname={pathname} />
        ))}
      </nav>
    </footer>
  );
}
