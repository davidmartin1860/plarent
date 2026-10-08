import Link from "next/link";
import { t } from "@/lib/i18n";

export default function NotFound() {
  return (
    <div className="rounded-[14px] border border-dashed border-secondary/40 p-8 text-center">
      <p className="text-secondary">{t("plants.detail.notFound")}</p>
      <Link href="/plants" className="mt-3 inline-block text-secondary-title underline underline-offset-2">
        {t("common.backToPlants")}
      </Link>
    </div>
  );
}
