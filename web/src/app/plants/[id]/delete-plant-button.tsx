"use client";

import { useState, useTransition } from "react";
import { Dialog } from "@/components/dialog";
import { t } from "@/lib/i18n";
import { deletePlantAction } from "./actions";

export function DeletePlantButton({ plantId, plantName }: { plantId: string; plantName: string }) {
  const [isOpen, setIsOpen] = useState(false);
  const [isPending, startTransition] = useTransition();

  return (
    <>
      <button
        type="button"
        className="text-sm text-red-600 underline underline-offset-2 hover:text-red-700 dark:text-red-400 dark:hover:text-red-300"
        onClick={() => setIsOpen(true)}
      >
        {t("plants.delete.button")}
      </button>

      <Dialog open={isOpen} title={t("plants.delete.title")} onClose={() => setIsOpen(false)}>
        <p className="mt-2 text-sm text-stone-600 dark:text-stone-400">
          {t("plants.delete.confirmMessage", { name: plantName })}
        </p>
        <div className="mt-6 flex justify-end gap-3">
          <button
            type="button"
            className="rounded-md px-3 py-1.5 text-sm text-stone-700 hover:bg-stone-100 dark:text-stone-300 dark:hover:bg-stone-800"
            onClick={() => setIsOpen(false)}
            disabled={isPending}
          >
            {t("common.cancel")}
          </button>
          <button
            type="button"
            className="rounded-md bg-red-600 px-3 py-1.5 text-sm text-white hover:bg-red-700 disabled:opacity-60 dark:bg-red-700 dark:hover:bg-red-600"
            onClick={() => startTransition(() => deletePlantAction(plantId))}
            disabled={isPending}
          >
            {isPending ? t("plants.delete.confirming") : t("plants.delete.confirm")}
          </button>
        </div>
      </Dialog>
    </>
  );
}
