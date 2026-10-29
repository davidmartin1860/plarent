import { cleanup, render, screen, within } from "@testing-library/react";
import { type ReactNode } from "react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { AppFooter } from "@/components/app-footer";

const usePathname = vi.fn(() => "/");

vi.mock("next/navigation", () => ({
  usePathname: () => usePathname(),
}));

vi.mock("next/link", () => ({
  default: ({
    href,
    children,
    ...props
  }: {
    href: string;
    children: ReactNode;
    [key: string]: unknown;
  }) => (
    <a href={href} {...props}>
      {children}
    </a>
  ),
}));

describe("AppFooter", () => {
  beforeEach(() => {
    usePathname.mockReturnValue("/");
  });

  afterEach(() => {
    cleanup();
  });

  it("renders four navigation links and a centered home logo", () => {
    render(<AppFooter />);

    const nav = screen.getByRole("navigation", { name: "Hauptnavigation" });
    const links = within(nav).getAllByRole("link");

    expect(links).toHaveLength(5);
    expect(links.map((link) => link.getAttribute("href"))).toEqual([
      "/plants",
      "/calendar",
      "/",
      "/points",
      "/profile",
    ]);
    expect(links.map((link) => link.textContent?.trim())).toEqual([
      "Meine Pflanzen",
      "Aufgaben-Kalender",
      "",
      "Plärr-Points",
      "Mein Profil",
    ]);
    expect(screen.getByRole("link", { name: "Startseite" })).toHaveAttribute("href", "/");
  });

  it("uses the larger logo on the home screen", () => {
    render(<AppFooter />);

    const home = screen.getByRole("link", { name: "Startseite" });

    expect(home).toHaveAttribute("aria-current", "page");
    expect(home).toHaveClass("h-[65px]", "w-[71px]");
    expect(screen.getByRole("link", { name: /Meine Pflanzen/ })).not.toHaveAttribute(
      "aria-current",
    );
  });

  it("uses the smaller logo and highlights the active tab away from home", () => {
    usePathname.mockReturnValue("/profile");
    render(<AppFooter />);

    expect(screen.getByRole("link", { name: "Startseite" })).toHaveClass("h-[47px]", "w-[51px]");
    expect(screen.getByRole("link", { name: /Mein Profil/ })).toHaveClass("text-highlight");
  });

  it("highlights My Plants on plant pages", () => {
    usePathname.mockReturnValue("/plants/1");
    render(<AppFooter />);

    const myPlants = screen.getByRole("link", { name: /Meine Pflanzen/ });

    expect(screen.getByRole("link", { name: "Startseite" })).toHaveClass(
      "h-[47px]",
      "w-[51px]",
    );
    expect(myPlants).toHaveAttribute("aria-current", "page");
    expect(myPlants).toHaveClass("text-highlight");
  });

  it("marks the current page link as active", () => {
    usePathname.mockReturnValue("/profile");
    render(<AppFooter />);

    const nav = screen.getByRole("navigation", { name: "Hauptnavigation" });

    expect(within(nav).getByRole("link", { name: /Mein Profil/ })).toHaveAttribute(
      "aria-current",
      "page",
    );
    expect(within(nav).getByRole("link", { name: /Meine Pflanzen/ })).not.toHaveAttribute(
      "aria-current",
    );
  });

  it("uses the tab bar color token", () => {
    const { container } = render(<AppFooter />);
    const footer = container.querySelector("footer");

    expect(footer).toHaveClass("bg-tab-bar");
    expect(footer).toHaveClass("text-primary");
  });

  it.each(["/login", "/register"])("is hidden on %s", (pathname) => {
    usePathname.mockReturnValue(pathname);

    render(<AppFooter />);

    expect(screen.queryByRole("navigation")).toBeNull();
  });
});
