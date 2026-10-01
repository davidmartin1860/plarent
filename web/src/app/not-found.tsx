import Link from "next/link";
import { t } from "@/lib/i18n";

export default function NotFound() {
  return (
    <div className="rounded-lg border border-dashed border-stone-300 p-8 text-center dark:border-stone-700">
      <p className="text-stone-600 dark:text-stone-400">{t("plants.detail.notFound")}</p>
      <Link href="/" className="mt-3 inline-block text-emerald-700 underline underline-offset-2 dark:text-emerald-400">
        {t("common.backToPlants")}
      </Link>
    </div>
  );
}
