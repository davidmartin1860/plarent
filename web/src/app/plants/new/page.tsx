import { t } from "@/lib/i18n";
import { createPlantAction } from "./actions";

export default function NewPlantPage() {
  return (
    <div className="rounded-lg border border-stone-200 bg-white p-6 shadow-sm dark:border-stone-800 dark:bg-stone-900">
      <h1 className="text-2xl font-semibold text-stone-900 dark:text-stone-100">{t("plants.new.title")}</h1>

      <form action={createPlantAction} className="mt-6 space-y-4">
        <Field label={t("plants.new.nameLabel")} name="name" required />
        <Field
          label={t("plants.new.speciesLabel")}
          name="species"
          placeholder={t("plants.new.speciesPlaceholder")}
        />
        <Field
          label={t("plants.new.locationLabel")}
          name="location"
          placeholder={t("plants.new.locationPlaceholder")}
        />
        <Field label={t("plants.new.acquiredDateLabel")} name="acquiredDate" type="date" />
        <div>
          <label htmlFor="notes" className="block text-sm font-medium text-stone-700 dark:text-stone-300">
            {t("plants.new.notesLabel")}
          </label>
          <textarea
            id="notes"
            name="notes"
            rows={3}
            className="mt-1 block w-full rounded-md border border-stone-300 px-3 py-2 shadow-sm focus:border-emerald-500 focus:outline-none focus:ring-1 focus:ring-emerald-500 dark:border-stone-700 dark:bg-stone-800 dark:text-stone-100"
          />
        </div>

        <button
          type="submit"
          className="rounded-md bg-emerald-700 px-4 py-2 font-medium text-white hover:bg-emerald-800 dark:bg-emerald-600 dark:hover:bg-emerald-700"
        >
          {t("plants.new.save")}
        </button>
      </form>
    </div>
  );
}

function Field({
  label,
  name,
  type = "text",
  required = false,
  placeholder,
}: {
  label: string;
  name: string;
  type?: string;
  required?: boolean;
  placeholder?: string;
}) {
  return (
    <div>
      <label htmlFor={name} className="block text-sm font-medium text-stone-700 dark:text-stone-300">
        {label}
        {required && <span className="text-emerald-700 dark:text-emerald-400"> *</span>}
      </label>
      <input
        id={name}
        name={name}
        type={type}
        required={required}
        placeholder={placeholder}
        className="mt-1 block w-full rounded-md border border-stone-300 px-3 py-2 shadow-sm focus:border-emerald-500 focus:outline-none focus:ring-1 focus:ring-emerald-500 dark:border-stone-700 dark:bg-stone-800 dark:text-stone-100"
      />
    </div>
  );
}
