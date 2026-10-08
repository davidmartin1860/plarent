import { t } from "@/lib/i18n";
import { createPlantAction } from "./actions";

export default function NewPlantPage() {
  return (
    <div className="rounded-[14px] border border-secondary/30 bg-boxes-secondary p-6 shadow-[0_5px_16px_rgba(53,66,56,0.07)]">
      <h1 className="type-title text-primary">{t("plants.new.title")}</h1>

      <form action={createPlantAction} className="mt-6 space-y-4">
        <Field label={t("plants.new.nameLabel")} name="name" required />
        <Field
          label={t("plants.new.speciesLabel")}
          name="species"
          required
          placeholder={t("plants.new.speciesPlaceholder")}
        />
        <Field
          label={t("plants.new.locationLabel")}
          name="location"
          placeholder={t("plants.new.locationPlaceholder")}
        />
        <Field label={t("plants.new.acquiredDateLabel")} name="acquiredDate" type="date" />
        <div>
          <label htmlFor="notes" className="block font-sans text-sm font-medium text-secondary">
            {t("plants.new.notesLabel")}
          </label>
          <textarea
            id="notes"
            name="notes"
            rows={3}
            className="mt-1 block w-full rounded-[14px] border border-secondary/40 bg-background px-3 py-2 font-sans text-primary shadow-sm outline-none focus:border-secondary-title focus:ring-1 focus:ring-secondary-title"
          />
        </div>

        <button
          type="submit"
          className="rounded-full bg-secondary-title px-4 py-2 font-sans font-semibold text-background hover:opacity-90"
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
      <label htmlFor={name} className="block font-sans text-sm font-medium text-secondary">
        {label}
        {required && <span className="text-accent"> *</span>}
      </label>
      <input
        id={name}
        name={name}
        type={type}
        required={required}
        placeholder={placeholder}
        className="mt-1 block w-full rounded-[14px] border border-secondary/40 bg-background px-3 py-2 font-sans text-primary shadow-sm outline-none focus:border-secondary-title focus:ring-1 focus:ring-secondary-title"
      />
    </div>
  );
}
