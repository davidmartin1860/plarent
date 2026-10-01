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
        className="w-full max-w-sm rounded-lg bg-white p-6 shadow-lg dark:bg-stone-900"
        onClick={(event) => event.stopPropagation()}
      >
        <h2 id="dialog-title" className="text-lg font-semibold text-stone-900 dark:text-stone-100">
          {title}
        </h2>
        {children}
      </div>
    </div>
  );
}
