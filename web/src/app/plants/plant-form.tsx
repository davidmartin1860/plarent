import Link from "next/link";
import type { Translator } from "@/lib/i18n";
import { getTranslations } from "@/lib/i18n/server";
import type { Plant } from "@/lib/types";
import { ImagePicker } from "./image-picker";
import { plantImageUrl } from "./plant-form-data";

// Shared by the new and edit screens. When `plant` is given, the fields start with its values.
export async function PlantForm({
  action,
  plant,
}: {
  action: (formData: FormData) => Promise<void>;
  plant?: Plant;
}) {
  const t = await getTranslations();

  return (
    <form action={action} className="flex flex-col gap-[14px]">
      <div className="flex justify-end">
        <ImagePicker savedImageUrl={plant ? plantImageUrl(plant) : null} />
      </div>

      <TextField name="name" placeholder={t("plants.new.namePlaceholder")} defaultValue={plant?.name} required />
      <TextField
        name="species"
        placeholder={t("plants.new.speciesPlaceholder")}
        defaultValue={plant?.species}
        required
      />
      <TextField
        name="location"
        placeholder={t("plants.new.locationPlaceholder")}
        defaultValue={plant?.location ?? undefined}
      />

      {/* The API has no care schedule yet, so these intervals are not saved. */}
      <fieldset className="flex flex-col gap-3 rounded-[20px] border border-secondary px-[14px] py-4">
        <IntervalField name="watering" label={t("plants.new.wateringLabel")} t={t} />
        <IntervalField name="misting" label={t("plants.new.mistingLabel")} t={t} />
      </fieldset>

      <button
        type="submit"
        className="h-[43px] self-end rounded-[10px] bg-boxes-primary px-[13px] font-sans text-sm font-semibold text-secondary hover:opacity-90"
      >
        {t("plants.new.save")}
      </button>
    </form>
  );
}

export async function BackLink({ href }: { href: string }) {
  const t = await getTranslations();

  return (
    <Link
      href={href}
      className="flex h-[43px] w-fit items-center gap-1 rounded-[10px] bg-boxes-primary px-[14px] font-sans text-sm font-semibold text-secondary hover:opacity-90"
    >
      <ChevronLeft />
      {t("plants.new.back")}
    </Link>
  );
}

function TextField({
  name,
  placeholder,
  defaultValue,
  required = false,
}: {
  name: string;
  placeholder: string;
  defaultValue?: string;
  required?: boolean;
}) {
  return (
    <input
      name={name}
      type="text"
      required={required}
      placeholder={placeholder}
      defaultValue={defaultValue}
      aria-label={placeholder}
      className="h-10 w-full rounded-[20px] border border-secondary bg-transparent px-[14px] font-sans text-sm font-semibold text-primary outline-none placeholder:text-secondary focus:border-secondary-title focus:ring-1 focus:ring-secondary-title"
    />
  );
}

function IntervalField({ name, label, t }: { name: string; label: string; t: Translator }) {
  const pill =
    "h-5 rounded-full leading-none border border-secondary bg-transparent text-center text-secondary outline-none focus:border-secondary-title focus:ring-1 focus:ring-secondary-title";

  return (
    <div className="type-body flex flex-col gap-1 text-primary">
      <span>{label}</span>
      <div className="flex items-center gap-2">
        <span>{t("plants.new.every")}</span>
        <input
          name={`${name}Interval`}
          type="number"
          min={1}
          defaultValue={1}
          aria-label={`${label} ${t("plants.new.intervalAmount")}`}
          className={`${pill} w-11 [appearance:textfield] [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none`}
        />
        <select
          name={`${name}Unit`}
          defaultValue="days"
          aria-label={`${label} ${t("plants.new.intervalUnit")}`}
          className={`${pill} w-20 appearance-none [text-align-last:center]`}
        >
          <option value="days">{t("plants.new.days")}</option>
          <option value="weeks">{t("plants.new.weeks")}</option>
        </select>
      </div>
    </div>
  );
}

function ChevronLeft() {
  return (
    <svg width="8" height="13" viewBox="0 0 8 13" fill="none" aria-hidden="true">
      <path d="M6.5 1.5 1.5 6.5l5 5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}
