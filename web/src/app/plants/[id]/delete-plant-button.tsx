"use client";

import { useState, useTransition } from "react";
import { deletePlantAction } from "./actions";

export function DeletePlantButton({ plantId, plantName }: { plantId: string; plantName: string }) {
  const [isOpen, setIsOpen] = useState(false);
  const [isPending, startTransition] = useTransition();

  return (
    <>
      <button
        type="button"
        className="text-sm text-red-600 underline underline-offset-2 hover:text-red-700"
        onClick={() => setIsOpen(true)}
      >
        Delete plant
      </button>

      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-sm rounded-lg bg-white p-6 shadow-lg">
            <h2 className="text-lg font-semibold text-stone-900">Delete plant</h2>
            <p className="mt-2 text-sm text-stone-600">
              Are you sure you want to delete <span className="font-medium">{plantName}</span>?
              This cannot be undone.
            </p>
            <div className="mt-6 flex justify-end gap-3">
              <button
                type="button"
                className="rounded-md px-3 py-1.5 text-sm text-stone-700 hover:bg-stone-100"
                onClick={() => setIsOpen(false)}
                disabled={isPending}
              >
                Cancel
              </button>
              <button
                type="button"
                className="rounded-md bg-red-600 px-3 py-1.5 text-sm text-white hover:bg-red-700 disabled:opacity-60"
                onClick={() => startTransition(() => deletePlantAction(plantId))}
                disabled={isPending}
              >
                {isPending ? "Deleting…" : "Delete"}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
