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
    <div className="rounded-[14px] border border-secondary/30 bg-boxes-secondary p-6 shadow-[0_5px_16px_rgba(53,66,56,0.07)]">
      <div className="flex items-center justify-between">
        <Link href="/" className="text-sm text-secondary-title underline underline-offset-2">
          ← {t("common.backToPlants")}
        </Link>
        <DeletePlantButton plantId={plant.id} plantName={plant.name} />
      </div>
      <h1 className="type-title mt-3 text-primary">{plant.name}</h1>
      <p className="italic text-secondary">{plant.species}</p>

      <dl className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <dt className="type-small text-secondary">{t("plants.detail.location")}</dt>
          <dd className="text-primary">{plant.location ?? "—"}</dd>
        </div>
        <div>
          <dt className="type-small text-secondary">{t("plants.detail.acquired")}</dt>
          <dd className="text-primary">{plant.acquiredDate ?? "—"}</dd>
        </div>
      </dl>

      {plant.notes && (
        <div className="mt-6">
          <dt className="type-small text-secondary">{t("plants.detail.notes")}</dt>
          <dd className="whitespace-pre-wrap text-primary">{plant.notes}</dd>
        </div>
      )}
    </div>
  );
}
