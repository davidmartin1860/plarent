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
    <div className="rounded-lg border border-red-200 bg-red-50 p-6 text-center">
      <p className="font-medium text-red-800">{t("error.title")}</p>
      <p className="mt-1 text-sm text-red-700">{error.message}</p>
      <button
        onClick={reset}
        className="mt-4 rounded-md bg-red-700 px-3 py-1.5 text-sm font-medium text-white hover:bg-red-800"
      >
        {t("error.retry")}
      </button>
    </div>
  );
}
