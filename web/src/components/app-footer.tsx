"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { type ReactNode } from "react";
import { Logo } from "@/components/logo";
import { t, type TranslationKey } from "@/lib/i18n";

type FooterLink = {
  kind: "link";
  href: string;
  labelKey: TranslationKey;
  Icon: () => ReactNode;
};

type FooterLogo = {
  kind: "logo";
  href: string;
};

type FooterItem = FooterLink | FooterLogo;

const FOOTER_ITEMS: FooterItem[] = [
  {
    kind: "link",
    href: "/",
    labelKey: "nav.myPlants",
    Icon: PlantIcon,
  },
  {
    kind: "link",
    href: "/calendar",
    labelKey: "nav.taskCalendar",
    Icon: CalendarIcon,
  },
  {
    kind: "logo",
    href: "/",
  },
  {
    kind: "link",
    href: "/points",
    labelKey: "nav.plaerrPoints",
    Icon: PointsIcon,
  },
  {
    kind: "link",
    href: "/profile",
    labelKey: "nav.myProfile",
    Icon: ProfileIcon,
  },
];

function isActivePath(pathname: string, href: string): boolean {
  if (href === "/") {
    return pathname === "/";
  }
  return pathname === href || pathname.startsWith(`${href}/`);
}

export function AppFooter() {
  const pathname = usePathname();

  return (
    <footer className="fixed inset-x-0 bottom-0 z-40 border-t border-secondary/30 bg-tab-bar pb-[env(safe-area-inset-bottom)] text-primary">
      <nav
        aria-label="Main"
        className="mx-auto flex max-w-3xl items-end justify-between gap-1 px-2 pt-2 pb-2"
      >
        {FOOTER_ITEMS.map((item) => {
          switch (item.kind) {
            case "logo":
              return (
                <Link
                  key="logo"
                  href={item.href}
                  aria-label={t("common.home")}
                  className="-mt-5 flex size-14 shrink-0 items-center justify-center rounded-full bg-tab-bar shadow-md ring-2 ring-secondary-title/30 transition hover:ring-secondary-title/60"
                >
                  <Logo className="size-10" />
                </Link>
              );
            case "link": {
              const label = t(item.labelKey);
              const active = isActivePath(pathname, item.href);
              const Icon = item.Icon;

              return (
                <Link
                  key={item.href}
                  href={item.href}
                  aria-current={active ? "page" : undefined}
                  className={[
                    "flex min-w-0 flex-1 flex-col items-center gap-1 rounded-md px-1 py-1 text-center transition",
                    active
                      ? "text-primary"
                      : "text-primary/70 hover:text-primary",
                  ].join(" ")}
                >
                  <span className="[&>svg]:size-5">
                    <Icon />
                  </span>
                  <span className="max-w-full truncate font-serif text-[0.8125rem] leading-none font-bold">
                    {label}
                  </span>
                </Link>
              );
            }
            default: {
              const _exhaustive: never = item;
              return _exhaustive;
            }
          }
        })}
      </nav>
    </footer>
  );
}

function PlantIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M12 21v-8M12 13c0-4 3-7 7-8-1 5-4 8-7 8ZM12 13c0-4-3-7-7-8 1 5 4 8 7 8Z"
        stroke="currentColor"
        strokeWidth="1.75"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function CalendarIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <rect
        x="3.75"
        y="5.75"
        width="16.5"
        height="14.5"
        rx="2"
        stroke="currentColor"
        strokeWidth="1.75"
      />
      <path
        d="M8 3.75v3.5M16 3.75v3.5M3.75 10.5h16.5"
        stroke="currentColor"
        strokeWidth="1.75"
        strokeLinecap="round"
      />
    </svg>
  );
}

function PointsIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <circle cx="12" cy="12" r="8.25" stroke="currentColor" strokeWidth="1.75" />
      <path
        d="M12 8.5v7M9.5 10.25h3.25a1.75 1.75 0 1 1 0 3.5H9.5"
        stroke="currentColor"
        strokeWidth="1.75"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function ProfileIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <circle cx="12" cy="9" r="3.25" stroke="currentColor" strokeWidth="1.75" />
      <path
        d="M6.5 18.25c1.4-2.2 3.3-3.25 5.5-3.25s4.1 1.05 5.5 3.25"
        stroke="currentColor"
        strokeWidth="1.75"
        strokeLinecap="round"
      />
    </svg>
  );
}
