"use client";

import { type ReactNode } from "react";

export function Dialog({
  open,
  title,
  children,
  onClose,
}: {
  open: boolean;
  title: string;
  children: ReactNode;
  onClose: () => void;
}) {
  if (!open) {
    return null;
  }

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 dark:bg-black/60"
      role="presentation"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="dialog-title"
        className="w-full max-w-sm rounded-[14px] bg-boxes-secondary p-6 text-primary shadow-[0_5px_16px_rgba(53,66,56,0.07)]"
        onClick={(event) => event.stopPropagation()}
      >
        <h2 id="dialog-title" className="font-display text-lg font-semibold text-primary">
          {title}
        </h2>
        {children}
      </div>
    </div>
  );
}
