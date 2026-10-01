import Link from "next/link";
import { getPlants } from "@/lib/api";
import { t } from "@/lib/i18n";

export default async function PlantsPage() {
  const plants = await getPlants();

  if (plants.length === 0) {
    return (
      <div className="rounded-lg border border-dashed border-stone-300 p-8 text-center dark:border-stone-700">
        <p className="text-stone-600 dark:text-stone-400">{t("plants.list.empty")}</p>
        <Link
          href="/plants/new"
          className="mt-3 inline-block text-emerald-700 underline underline-offset-2 dark:text-emerald-400"
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
            className="block rounded-lg border border-stone-200 bg-white p-4 shadow-sm transition hover:border-emerald-300 hover:shadow dark:border-stone-800 dark:bg-stone-900 dark:hover:border-emerald-700"
          >
            <p className="font-medium text-stone-900 dark:text-stone-100">{plant.name}</p>
            <p className="text-sm italic text-stone-500 dark:text-stone-400">{plant.species}</p>
            {plant.location && (
              <p className="mt-1 text-sm text-stone-600 dark:text-stone-400">
                {t("plants.list.location", { location: plant.location })}
              </p>
            )}
          </Link>
        </li>
      ))}
    </ul>
  );
}
