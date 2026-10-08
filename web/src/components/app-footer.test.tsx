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

    const nav = screen.getByRole("navigation", { name: "Main" });
    const links = within(nav).getAllByRole("link");

    expect(links).toHaveLength(5);
    expect(links.map((link) => link.getAttribute("href"))).toEqual([
      "/",
      "/calendar",
      "/",
      "/points",
      "/profile",
    ]);
    expect(links.map((link) => link.textContent?.trim())).toEqual([
      "My Plants",
      "Task Calendar",
      "",
      "Plärr Points",
      "My Profile",
    ]);
    expect(screen.getByRole("link", { name: "Home" })).toHaveAttribute("href", "/");
  });

  it("marks the current page link as active", () => {
    usePathname.mockReturnValue("/profile");
    render(<AppFooter />);

    const nav = screen.getByRole("navigation", { name: "Main" });

    expect(within(nav).getByRole("link", { name: /My Profile/ })).toHaveAttribute(
      "aria-current",
      "page",
    );
    expect(within(nav).getByRole("link", { name: /My Plants/ })).not.toHaveAttribute(
      "aria-current",
    );
  });

  it("uses the tab bar color token", () => {
    const { container } = render(<AppFooter />);
    const footer = container.querySelector("footer");

    expect(footer).toHaveClass("bg-tab-bar");
    expect(footer).toHaveClass("text-primary");
  });
});
