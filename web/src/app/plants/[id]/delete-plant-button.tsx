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
        className="text-sm text-accent underline underline-offset-2 hover:opacity-80"
        onClick={() => setIsOpen(true)}
      >
        {t("plants.delete.button")}
      </button>

      <Dialog open={isOpen} title={t("plants.delete.title")} onClose={() => setIsOpen(false)}>
        <p className="mt-2 text-sm text-secondary">
          {t("plants.delete.confirmMessage", { name: plantName })}
        </p>
        <div className="mt-6 flex justify-end gap-3">
          <button
            type="button"
            className="rounded-full px-3 py-1.5 font-sans text-sm text-secondary hover:bg-boxes-primary"
            onClick={() => setIsOpen(false)}
            disabled={isPending}
          >
            {t("common.cancel")}
          </button>
          <button
            type="button"
            className="rounded-full bg-accent px-3 py-1.5 font-sans text-sm font-semibold text-almost-white hover:opacity-90 disabled:opacity-60"
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
