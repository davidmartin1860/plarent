import { notFound } from "next/navigation";
import Link from "next/link";
import { getPlant } from "@/lib/api";
import { t } from "@/lib/i18n";
import { DeletePlantButton } from "./delete-plant-button";

export default async function PlantDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const plant = await getPlant(id);

  if (!plant) {
    notFound();
  }

  return (
    <div className="rounded-lg border border-stone-200 bg-white p-6 shadow-sm">
      <div className="flex items-center justify-between">
        <Link href="/" className="text-sm text-emerald-700 underline underline-offset-2">
          ← {t("common.backToPlants")}
        </Link>
        <DeletePlantButton plantId={plant.id} plantName={plant.name} />
      </div>
      <h1 className="mt-3 text-2xl font-semibold text-stone-900">{plant.name}</h1>
      {plant.species && <p className="italic text-stone-500">{plant.species}</p>}

      <dl className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <dt className="text-xs uppercase tracking-wide text-stone-400">
            {t("plants.detail.location")}
          </dt>
          <dd className="text-stone-800">{plant.location ?? "—"}</dd>
        </div>
        <div>
          <dt className="text-xs uppercase tracking-wide text-stone-400">
            {t("plants.detail.acquired")}
          </dt>
          <dd className="text-stone-800">{plant.acquiredDate ?? "—"}</dd>
        </div>
      </dl>

      {plant.notes && (
        <div className="mt-6">
          <dt className="text-xs uppercase tracking-wide text-stone-400">
            {t("plants.detail.notes")}
          </dt>
          <dd className="whitespace-pre-wrap text-stone-800">{plant.notes}</dd>
        </div>
      )}
    </div>
  );
}
