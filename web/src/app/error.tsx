"use client";

import { t } from "@/lib/i18n";

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <div className="rounded-[14px] border border-accent/40 bg-boxes-secondary p-6 text-center">
      <p className="font-sans font-semibold text-accent">{t("error.title")}</p>
      <p className="mt-1 text-sm text-secondary">{error.message}</p>
      <button
        onClick={reset}
        className="mt-4 rounded-full bg-accent px-3 py-1.5 font-sans text-sm font-semibold text-almost-white hover:opacity-90"
      >
        {t("error.retry")}
      </button>
    </div>
  );
}
