import Link from "next/link";
import { getPlants } from "@/lib/api";
import { t } from "@/lib/i18n";

export default async function PlantsPage() {
  const plants = await getPlants();

  if (plants.length === 0) {
    return (
      <div className="rounded-[14px] border border-dashed border-secondary/40 p-8 text-center">
        <p className="text-secondary">{t("plants.list.empty")}</p>
        <Link
          href="/plants/new"
          className="mt-3 inline-block text-secondary-title underline underline-offset-2"
        >
          {t("plants.list.addFirst")}
        </Link>
      </div>
    );
  }

  return (
    <ul className="grid gap-3 sm:grid-cols-2">
      {plants.map((plant) => (
        <li key={plant.id}>
          <Link
            href={`/plants/${plant.id}`}
            className="block rounded-[14px] border border-secondary/30 bg-boxes-secondary p-4 shadow-[0_5px_16px_rgba(53,66,56,0.07)] transition hover:border-secondary-title"
          >
            <p className="font-sans text-sm font-semibold text-primary">{plant.name}</p>
            <p className="text-sm italic text-secondary">{plant.species}</p>
            {plant.location && (
              <p className="mt-1 text-sm text-secondary">
                {t("plants.list.location", { location: plant.location })}
              </p>
            )}
          </Link>
        </li>
      ))}
    </ul>
  );
}
